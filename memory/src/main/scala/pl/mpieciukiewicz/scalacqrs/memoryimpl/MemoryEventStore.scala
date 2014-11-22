package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.time.Clock

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.exception.{NoEventsForAggregateException, ConcurrentAggregateModificationException}
import pl.mpieciukiewicz.scalacqrs.internal.AbstractEvent

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MemoryEventStore(clock: Clock) extends EventStore {

  private val eventsByType = mutable.Map[Class[_], mutable.Map[UID, ListBuffer[EventRow[_]]]]()

  override def addEventForNewAggregate[T](userId: UID, newAggregateId: UID, event: CreationEvent[T]): Unit = {
    val eventsForType = eventsByType.getOrElseUpdate(event.entityClass, new mutable.HashMap[UID, ListBuffer[EventRow[_]]])
    val eventsForEntity = eventsForType.getOrElseUpdate(newAggregateId, new ListBuffer[EventRow[_]])
    val eventRow = EventRow(userId, newAggregateId, 0, clock.instant(), event)
    eventsForEntity += eventRow
  }

  override def addEventForExistingAggregate[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: ModificationEvent[T]): Unit = {
      val eventsForType = eventsByType.getOrElse(event.entityClass, mutable.Map())
      val eventsForEntity = eventsForType.getOrElseUpdate(aggregateId, new ListBuffer[EventRow[_]])
      
      if (eventsForEntity.size > expectedVersion) {
        throw new ConcurrentAggregateModificationException("Expected version " + expectedVersion + " but is " + eventsForEntity.size)
      }

      eventsForEntity += EventRow(userId, aggregateId, expectedVersion, clock.instant(), event)
  }

  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: UID): List[EventRow[T]] = {
    eventsByType.
      getOrElse(aggregateClass, throw new NoEventsForAggregateException("No events found for type " + aggregateClass)).
      getOrElse(uid, throw new NoEventsForAggregateException("No events found for type " + aggregateClass + " with uid " + uid)).
      toList.asInstanceOf[List[EventRow[T]]]
  }

  override def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: UID, fromVersion: Int): List[EventRow[T]] = {
    getEventsForAggregate(aggregateClass, uid).drop(fromVersion)
  }

  override def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: UID, toVersion: Int): List[EventRow[T]] = {
    getEventsForAggregate(aggregateClass, uid).take(toVersion)
  }


}
