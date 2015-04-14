package io.scalacqrs.core

import java.lang.reflect.Type

import io.scalacqrs._
import io.scalacqrs.data.AggregateId
import io.scalacqrs.event.{DuplicationEvent, Event, EventRow, UndoEvent}
import io.scalacqrs.eventhandler._
import io.scalacqrs.exception.{AggregateWasAlreadyDeletedException, IncorrectAggregateVersionException, NoEventsForAggregateException}
import org.slf4j.LoggerFactory
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

import scala.util.Try

abstract class CoreDataStore[A](
    val eventStore: EventStore, handlers: Seq[EventHandler[A, _ <: Event[A]]])
  extends DataStore[A](eventStore) {

  // constructor

  handlers.foreach(registerHandler)

  // fields

  private val Log = LoggerFactory.getLogger(classOf[CoreDataStore[A]])

  val aggregateClass: Class[A] = {
    var clazz = this.getClass.asInstanceOf[Class[_]]
    while(clazz.getGenericSuperclass.isInstanceOf[Class[_]]) {
        clazz = clazz.getGenericSuperclass.asInstanceOf[Class[_]]
    }
    val arguments: Array[Type] = clazz.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[A]]
  }

  // methods

  private def registerHandler(eventHandler: EventHandler[A, _ <: Event[A]]): Unit = {

    val eventClass = eventHandler.eventClass.asInstanceOf[Class[Event[A]]]
    eventHandlers += eventClass -> eventHandler
  }

  override def getAggregateByVersionAndApplyEventToIt(
                  id: AggregateId, version: Int, event: Event[A]): Try[Aggregate[A]] = event match {
    case e: UndoEvent[A] => getAggregateByVersion(id, version + 1)
    case _ => getAggregateWithOptionalVersion(id, Some(version))
                .map( a => updateAggregateWithEvent(event, a))
  }

  override def getAggregateByVersion(id: AggregateId, version: Int): Try[Aggregate[A]] =
    getAggregateWithOptionalVersion(id, Some(version))

  override def getAggregate(id: AggregateId): Try[Aggregate[A]] = getAggregateWithOptionalVersion(id, None)

  override def getAggregates(ids: Seq[AggregateId]): Seq[Aggregate[A]] = {
    //TODO for sure optimize for databases
    ids.map(getAggregateWithOptionalVersion(_, None).getOrElse(null)).filter(_ != null)
  }

  private def getAggregateWithOptionalVersion(
                           id: AggregateId, version: Option[Int]): Try[Aggregate[A]] = {
    // helper methods:
    def dbEventRows: Seq[EventRow[A]] = {
      if (version.isDefined) {
        if (version.get < 1) {
          throw new
              IncorrectAggregateVersionException("Cannot get aggregates for versions lower than 1")
        } else {
          eventStore.getEventsForAggregateToVersion(aggregateClass, id, version.get)
        }
      } else {
        eventStore.getEventsForAggregate(aggregateClass, id)
      }
    }
    def getAggregate(eventRow: EventRow[A]): Aggregate[A] = {
      val aggregateRoot = if (eventRow.version != 1) {
        throw new IllegalStateException(
          "CreatorEvent need to be of version 1, as it always first event for an aggregate. (" +
            eventRow.event.getClass + " has version " + eventRow.version + ")")
      } else {
          eventHandlers(eventRow.event.getClass.asInstanceOf[Class[Event[A]]]) match {
          case handler: CreationEventHandler[A, Event[A]] => handler.handleEvent(eventRow.event)
          case handler: DuplicationEventHandler[A, Event[A]] =>
            val event = eventRow.event.asInstanceOf[DuplicationEvent[A]]
            val baseAggregate = getAggregateWithOptionalVersion(event.baseAggregateId, Some(event.baseAggregateVersion))
            if(baseAggregate.isFailure) {
              throw new IllegalStateException("Unable to get base aggregate")
            } else if (baseAggregate.get.aggregateRoot.isEmpty) {
              throw new IllegalStateException("Base aggregate has already been deleted")
            } else {
              handler.handleEvent(baseAggregate.get.aggregateRoot.get, event)
            }

        }
      }
      Aggregate(id, 1, Some(aggregateRoot))
    }
    // Body:
    Try {
      val eventRows = dbEventRows

      if (eventRows.isEmpty) throw new NoEventsForAggregateException(
        "Aggregate of type " + aggregateClass + " does not exist.")

      val eventRowsUndoApplied = applyUndoEvents(eventRows)

      val creatorEventRow: EventRow[A] = eventRowsUndoApplied.head

      var aggregate = getAggregate(creatorEventRow)

      for (eventRow <- eventRowsUndoApplied.tail) {
        if (eventRow.version == aggregate.version + 1) {
          aggregate = updateAggregateWithEvent(eventRow.event, aggregate)
        } else
          throw new IllegalStateException(
            "Unexpected version for aggregate when applying eventRow. " + "[aggregateType:" + aggregateClass
              .getName + ", aggregateId:" + aggregate.uid + ", aggregateVersion:" +
              aggregate.version + ", eventType:" + eventRow.event.getClass.getName +
              ", expectedVersion:" + eventRow.version + "]")
      }

      if (Log.isDebugEnabled) {
        Log.debug(eventRows.size + " eventRows applied for aggregate [type:" +
          aggregateClass.getName + ", uid:" + aggregate.uid + "]")
      }
      aggregate
    }
  }
/** undo events should be handled before */
  protected def updateAggregateWithEvent(event: Event[A], aggregate: Aggregate[A]): Aggregate[A] = {
    def handleWith(handler: EventHandler[A, _]): Aggregate[A] = handler match {
      case h: ModificationEventHandler[A, _] => Aggregate(aggregate.uid,
        aggregate.version + 1,
        Some(h.asInstanceOf[ModificationEventHandler[A, Event[A]]]
          .handleEvent(aggregate.aggregateRoot.get, event)))
      case h: DeletionEventHandler[A, _] => Aggregate(aggregate.uid,
        aggregate.version + 1,
        None)
      case _ => throw new IllegalStateException(
        "No handler registered for event " + event.getClass.getName)
    }
    // Body:
    if (aggregate.aggregateRoot.isDefined) {
      if (event.isInstanceOf[NoopEvent[A]]) {
        Aggregate(aggregate.uid, aggregate.version + 1, aggregate.aggregateRoot)
      } else {
        val handler: EventHandler[A, _] = eventHandlers(
          event.getClass.asInstanceOf[Class[Event[A]]])
        handleWith(handler)
      }
    } else throw new AggregateWasAlreadyDeletedException(
        "Unexpected modification of already deleted aggregate")
  }

  def applyUndoEvents(events: Seq[EventRow[A]]): Seq[EventRow[A]] = {
    val noopEvent = NoopEvent[A]()
    var eventsAfterUndo = List[EventRow[A]]()
    var eventsStored = 0
    for(eventRow <- events) {
      eventsStored += 1
      eventRow.event match {
        case e: UndoEvent[A] =>
          var undoneCount = 0
          val (undone, notUndone) = eventsAfterUndo.span(eventRow => {
            if(eventRow.event.isInstanceOf[NoopEvent[A]]) {
              true
            } else {
              val test = undoneCount < e.eventsCount
              undoneCount += 1
              test
            }
          })
          eventsAfterUndo = eventRow.copy(event = noopEvent) ::
            undone.map(ev => ev.copy(event = noopEvent)) ::: notUndone
        case e: Event[A] => eventsAfterUndo ::= eventRow
      }
    }
    eventsAfterUndo.reverse
  }

  override def getAllAggregateIds(): Seq[AggregateId] = {
    eventStore.getAllAggregateIds[A](aggregateClass)
  }

  override def countAllAggregates(): Long = {
    eventStore.countAllAggregates(aggregateClass)
  }

}
