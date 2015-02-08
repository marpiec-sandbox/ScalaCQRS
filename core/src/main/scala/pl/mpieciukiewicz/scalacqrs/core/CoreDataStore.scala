package pl.mpieciukiewicz.scalacqrs.core

import org.slf4j.LoggerFactory
import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.{EventRow, Event}
import pl.mpieciukiewicz.scalacqrs.eventhandler.{EventHandler, CreationEventHandler, DeletionEventHandler, ModificationEventHandler}
import pl.mpieciukiewicz.scalacqrs.exception.AggregateWasAlreadyDeletedException

import scala.collection.mutable

class CoreDataStore[A](val eventStore: EventStore, aggregateClass: Class[A], handlers: Seq[EventHandler[A, _ <: Event[A]]]) extends DataStore[A] {

  private val Log = LoggerFactory.getLogger(classOf[CoreDataStore[A]])
  
  private val eventHandlers = mutable.HashMap[Class[A], mutable.HashMap[Class[Event[A]], EventHandler[A, _ <: Event[A]]]]()

  handlers.foreach(registerHandler)


  private def registerHandler(eventHandler: EventHandler[A, _ <: Event[A]]): Unit = {
    val eventClass = eventHandler.eventClass.asInstanceOf[Class[Event[A]]]
    val handlers = eventHandlers.getOrElse(aggregateClass, {
      val aggregateEventHandlers = mutable.HashMap[Class[Event[A]], EventHandler[A, _ <: Event[A]]]()
      eventHandlers += aggregateClass -> aggregateEventHandlers
      aggregateEventHandlers
    })
    handlers += eventClass -> eventHandler
  }


  override def getAggregateByVersion(id: AggregateId, version: Int): Aggregate[A] = getAggregateWithOptionalVersion(id, Some(version))

  override def getAggregate(id: AggregateId): Aggregate[A] = getAggregateWithOptionalVersion(id, None)

  override def getAggregates(ids: Seq[AggregateId]): Map[AggregateId, Aggregate[A]] = {
    //TODO for sure optimize for databases
    val aggregates: Seq[Aggregate[A]] = ids.map(getAggregateWithOptionalVersion(_, None))
    ids.zip(aggregates).toMap
  }

  private def getAggregateWithOptionalVersion(id: AggregateId, version: Option[Int]): Aggregate[A] = {
    val eventRows = if (version.isDefined) {
      if (version.get < 1) {
        throw new IllegalArgumentException("Cannot get aggregates for versions lower than 1")
      } else {
        eventStore.getEventsForAggregateToVersion(aggregateClass, id, version.get)
      }
    } else {
      eventStore.getEventsForAggregate(aggregateClass, id)
    }

    if (eventRows.isEmpty) {
      throw new IllegalStateException("Aggregate of type " + aggregateClass + " does not exist.")
    }


    val creatorEventRow: EventRow[A] = eventRows.head
    if (creatorEventRow.version != 1) {
      throw new IllegalStateException("CreatorEvent need to be of version 1, as it always first event for an aggregate. ("+
        creatorEventRow.event.getClass+" has version "+creatorEventRow.version+")")
    }
    val handler: EventHandler[A, _] = eventHandlers(creatorEventRow.event.aggregateType)(creatorEventRow.event.getClass.asInstanceOf[Class[Event[A]]])
    val aggregateRoot = handler.asInstanceOf[CreationEventHandler[A, Event[A]]].handleEvent(creatorEventRow.event)

    var aggregate = Aggregate(id, 1, Some(aggregateRoot))

    eventRows.tail.foreach((eventRow) => {
      if (eventRow.version == aggregate.version + 1 && aggregate.aggregateRoot.isDefined) {
        val handler: EventHandler[A, _] = eventHandlers(eventRow.event.aggregateType)(eventRow.event.getClass.asInstanceOf[Class[Event[A]]])
        aggregate = handler match {
          case h: ModificationEventHandler[A, _] => Aggregate(aggregate.uid, aggregate.version + 1, Some(h.asInstanceOf[ModificationEventHandler[A, Event[A]]].handleEvent(aggregate.aggregateRoot.get, eventRow.event)))
          case h: DeletionEventHandler[A, _] => Aggregate(aggregate.uid, aggregate.version + 1, None)
        }
      } else if (aggregate.aggregateRoot.isEmpty) {
        throw new AggregateWasAlreadyDeletedException("Unexpected modification of already deleted aggregate")
      } else {
        throw new IllegalStateException("Unexpected version for aggregate when applying eventRow. " +
          "[aggregateType:" + aggregateClass.getName + ", aggregateId:" + id + ", aggregateVersion:" +
          aggregate.version + ", eventType:" + eventRow.getClass.getName + ", expectedVersion:" + eventRow.version + "]")
      }
    })

    if (Log.isDebugEnabled) {
      Log.debug(eventRows.size + " eventRows applied for aggregate [type:" + aggregateClass.getName + ", uid:" + id + "]")
    }
    aggregate
  }

  override def getAllAggregateIds(): Seq[AggregateId] = {
    eventStore.getAllAggregateIds[A](aggregateClass)
  }

  override def countAllAggregates(): Long = {
    eventStore.countAllAggregates(aggregateClass)
  }


}
