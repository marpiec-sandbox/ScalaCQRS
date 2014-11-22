package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.time.Clock

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.exception.{NoEventsForAggregateException, ConcurrentAggregateModificationException}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MemoryEventStore(clock: Clock) extends EventStore {

  private val eventsByType = mutable.Map[Class[_], mutable.Map[UID, ListBuffer[EventRow]]]()

  override def addEventForNewAggregate(userId: UID, newAggregateId: UID, event: Event[_ <: Aggregate]): Unit = {
    val eventsForType = eventsByType.getOrElseUpdate(event.aggregateClass, new mutable.HashMap[UID, ListBuffer[EventRow]])
    val eventsForEntity = eventsForType.getOrElseUpdate(newAggregateId, new ListBuffer[EventRow])
    val eventRow = EventRow(userId, newAggregateId, 0, clock.instant(), event)
    eventsForEntity += eventRow
  }

  override def addEventForExistingAggregate(userId: UID, aggregateId: UID, expectedVersion: Int, event: Event[_ <: Aggregate]): Unit = {
      val eventsForType = eventsByType.getOrElse(event.aggregateClass, mutable.Map())
      val eventsForEntity = eventsForType.getOrElseUpdate(aggregateId, new ListBuffer[EventRow])
      
      if (eventsForEntity.size > expectedVersion) {
        throw new ConcurrentAggregateModificationException("Expected version " + expectedVersion + " but is " + eventsForEntity.size)
      }

      eventsForEntity += EventRow(userId, aggregateId, expectedVersion, clock.instant(), event)
  }

  override def getEventsForAggregate(aggregateClass: Class[_ <: Aggregate], uid: UID): List[EventRow] = {
    eventsByType.
      getOrElse(aggregateClass, throw new NoEventsForAggregateException("No events found for type " + aggregateClass)).
      getOrElse(uid, throw new NoEventsForAggregateException("No events found for type " + aggregateClass + " with uid " + uid)).
      toList
  }

  override def getEventsForAggregateFromVersion(aggregateClass: Class[_ <: Aggregate], uid: UID, fromVersion: Int): List[EventRow] = {
    getEventsForAggregate(aggregateClass, uid).drop(fromVersion)
  }


}
