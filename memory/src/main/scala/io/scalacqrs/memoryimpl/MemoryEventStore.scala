package io.scalacqrs.memoryimpl

import java.time.Clock

import io.scalacqrs._
import io.scalacqrs.data.{UserId, AggregateId}
import io.scalacqrs.event.{EventRow, Event}
import io.scalacqrs.exception.{NoEventsForAggregateException, ConcurrentAggregateModificationException}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

class MemoryEventStore(clock: Clock) extends EventStore {

  private val eventsByType = mutable.Map[Class[_], mutable.Map[AggregateId, ListBuffer[EventRow[_]]]]()

  override def addFirstEvent[A: TypeTag, E <: Event[A]: TypeTag](commandId: CommandId, userId: UserId,
                             newAggregateId: AggregateId, event: E): Unit = {
    val eventsForType = eventsByType.getOrElseUpdate(
      event.aggregateType, new mutable.HashMap[AggregateId, ListBuffer[EventRow[_]]])
    val eventsForEntity = eventsForType.getOrElseUpdate(newAggregateId, new ListBuffer[EventRow[_]])
    val eventRow = EventRow(commandId, userId, newAggregateId, 1, clock.instant(), event)
    eventsForEntity += eventRow
    // this is always first event so version is constant
    callUpdateListeners(newAggregateId, 1, event)
  }

  override def addEvent[A: TypeTag, E <: Event[A]: TypeTag](commandId: CommandId, userId: UserId, aggregateId: AggregateId,
                        expectedVersion: Int, event: E): Unit = {
    val eventsForType = eventsByType.getOrElse(event.aggregateType, mutable.Map())
    val eventsForEntity = eventsForType.getOrElseUpdate(aggregateId, new ListBuffer[EventRow[_]])

    if (eventsForEntity.size > expectedVersion) {
      throw new ConcurrentAggregateModificationException("Expected version " +
        expectedVersion + " but is " + eventsForEntity.size)
    }
    val version: Int = expectedVersion + 1

    eventsForEntity += EventRow(commandId, userId, aggregateId, version, clock.instant(), event)
    callUpdateListeners(aggregateId, version, event)
  }

  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId)(implicit tag: TypeTag[T]): Seq[EventRow[T]] = {
    eventsByType
      .getOrElse(aggregateClass, throw new NoEventsForAggregateException(
        "No events found for type " + aggregateClass))
      .getOrElse(uid, throw new NoEventsForAggregateException(
        "No events found for type " + aggregateClass + " with uid " + uid))
      .toList.asInstanceOf[List[EventRow[T]]]
  }

  override def getEventsForAggregateFromVersion[T](
                  aggregateClass: Class[T], uid: AggregateId, fromVersion: Int)(implicit tag: TypeTag[T]): Seq[EventRow[T]] = {
    getEventsForAggregate(aggregateClass, uid).drop(fromVersion)
  }

  override def getEventsForAggregateToVersion[T](
                  aggregateClass: Class[T], uid: AggregateId, toVersion: Int)(implicit tag: TypeTag[T]): Seq[EventRow[T]] = {
    getEventsForAggregate(aggregateClass, uid).take(toVersion)
  }

  override def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId] = {
    eventsByType.get(aggregateClass).map(_.keys.toList).getOrElse(List())
  }

  override def countAllAggregates[T](aggregateClass: Class[T]): Long = {
    eventsByType.get(aggregateClass).map(_.size.toLong).getOrElse(0L)
  }

}
