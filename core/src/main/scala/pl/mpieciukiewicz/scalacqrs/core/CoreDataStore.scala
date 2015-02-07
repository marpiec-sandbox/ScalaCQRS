package pl.mpieciukiewicz.scalacqrs.core

import org.slf4j.LoggerFactory
import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.{EventRow, Event}
import pl.mpieciukiewicz.scalacqrs.eventhandler.{EventHandler, CreationEventHandler, DeletionEventHandler, ModificationEventHandler}
import pl.mpieciukiewicz.scalacqrs.exception.AggregateWasAlreadyDeletedException

import scala.collection.mutable

class CoreDataStore(val eventStore: EventStore) extends DataStore {

  private val Log = LoggerFactory.getLogger(classOf[CoreDataStore])
  
  private val eventHandlers = mutable.HashMap[Class[_], mutable.HashMap[Class[Event[_]], EventHandler[_, _ <: Event[_]]]]()

  override def registerHandler[A, E <: Event[A]](eventHandler: EventHandler[A, E]): Unit = {
    val aggregateClass = eventHandler.aggregateClass
    val eventClass = eventHandler.eventClass.asInstanceOf[Class[Event[_]]]
    val handlers: mutable.HashMap[Class[Event[_]], EventHandler[_, _ <: Event[_]]] = eventHandlers.getOrElse(aggregateClass, {
      val aggregateEventHandlers = mutable.HashMap[Class[Event[_]], EventHandler[_, _ <: Event[_]]]()
      eventHandlers += aggregateClass -> aggregateEventHandlers
      aggregateEventHandlers
    })
    handlers += eventClass -> eventHandler
  }


  override def getAggregateByVersion[T](aggregateClass: Class[T], uid: AggregateId, version: Int): Aggregate[T] = getAggregateWithOptionalVersion(aggregateClass, uid, Some(version))

  override def getAggregate[T](aggregateClass: Class[T], uid: AggregateId): Aggregate[T] = getAggregateWithOptionalVersion(aggregateClass, uid, None)

  override def getAggregates[T](aggregateClass: Class[T], ids: Seq[AggregateId]): Map[AggregateId, Aggregate[T]] = {
    //TODO for sure optimize for databases
    val aggregates: Seq[Aggregate[T]] = ids.map(getAggregateWithOptionalVersion(aggregateClass, _, None))
    ids.zip(aggregates).toMap
  }

  private def getAggregateWithOptionalVersion[T](aggregateRootClass: Class[T], uid: AggregateId, version: Option[Int]): Aggregate[T] = {
    val eventRows = if (version.isDefined) {
      if (version.get < 1) {
        throw new IllegalArgumentException("Cannot get aggregates for versions lower than 1")
      } else {
        eventStore.getEventsForAggregateToVersion(aggregateRootClass, uid, version.get)
      }
    } else {
      eventStore.getEventsForAggregate(aggregateRootClass, uid)
    }

    if (eventRows.isEmpty) {
      throw new IllegalStateException("Aggregate of type " + aggregateRootClass + " does not exist.")
    }


    val creatorEventRow: EventRow[T] = eventRows.head
    if (creatorEventRow.version != 1) {
      throw new IllegalStateException("CreatorEvent need to be of version 1, as it always first event for an aggregate. ("+
        creatorEventRow.event.getClass+" has version "+creatorEventRow.version+")")
    }
    val handler: EventHandler[_, _ <: Event[_]] = eventHandlers(creatorEventRow.event.aggregateType)(creatorEventRow.event.getClass.asInstanceOf[Class[Event[_]]])
    val aggregateRoot = handler.asInstanceOf[CreationEventHandler[T, Event[T]]].handleEvent(creatorEventRow.event)

    var aggregate = Aggregate(uid, 1, Some(aggregateRoot))

    eventRows.tail.foreach((eventRow) => {
      if (eventRow.version == aggregate.version + 1 && aggregate.aggregateRoot.isDefined) {
        val handler: EventHandler[_, _ <: Event[_]] = eventHandlers(eventRow.event.aggregateType)(eventRow.event.getClass.asInstanceOf[Class[Event[_]]])
        aggregate = handler match {
          case h: ModificationEventHandler[T, Event[T]] => Aggregate(aggregate.uid, aggregate.version + 1, Some(h.handleEvent(aggregate.aggregateRoot.get, eventRow.event)))
          case h: DeletionEventHandler[T, Event[T]] => Aggregate(aggregate.uid, aggregate.version + 1, None)
        }
      } else if (aggregate.aggregateRoot.isEmpty) {
        throw new AggregateWasAlreadyDeletedException("Unexpected modification of already deleted aggregate")
      } else {
        throw new IllegalStateException("Unexpected version for aggregate when applying eventRow. " +
          "[aggregateType:" + aggregateRootClass.getName + ", aggregateId:" + uid + ", aggregateVersion:" +
          aggregate.version + ", eventType:" + eventRow.getClass.getName + ", expectedVersion:" + eventRow.version + "]")
      }
    })

    if (Log.isDebugEnabled) {
      Log.debug(eventRows.size + " eventRows applied for aggregate [type:" + aggregateRootClass.getName + ", uid:" + uid + "]")
    }
    aggregate
  }

  override def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId] = {
    eventStore.getAllAggregateIds[T](aggregateClass)
  }

  override def countAllAggregates[T](aggregateClass: Class[T]): Long = {
    eventStore.countAllAggregates(aggregateClass)
  }


}
