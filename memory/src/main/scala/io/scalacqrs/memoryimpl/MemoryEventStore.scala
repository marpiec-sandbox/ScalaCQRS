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

  private val eventsByType = mutable.Map[Type, mutable.Map[AggregateId, ListBuffer[EventRow[Event[AnyRef]]]]]()

  override def addFirstEvent[E <: Event[_]: TypeTag](commandId: CommandId, userId: UserId,
                             newAggregateId: AggregateId, event: E): Unit = {
    val eventsForType = eventsByType.getOrElseUpdate(
      event.aggregateType, new mutable.HashMap[AggregateId, ListBuffer[EventRow[Event[AnyRef]]]])
    val eventsForEntity = eventsForType.getOrElseUpdate(newAggregateId, new ListBuffer[EventRow[Event[AnyRef]]])
    val eventRow = EventRow(commandId, userId, newAggregateId, 1, clock.instant(), event)
    eventsForEntity += eventRow.asInstanceOf[EventRow[Event[AnyRef]]]
    // this is always first event so version is constant
    callUpdateListeners(newAggregateId, 1, event)
  }

  override def addEvent[E <: Event[_]: TypeTag](commandId: CommandId, userId: UserId, aggregateId: AggregateId,
                        expectedVersion: Int, event: E): Unit = {
    val eventsForType = eventsByType.getOrElse(event.aggregateType, mutable.Map())
    val eventsForEntity = eventsForType.getOrElseUpdate(aggregateId, new ListBuffer[EventRow[Event[AnyRef]]])

    if (eventsForEntity.size > expectedVersion) {
      throw new ConcurrentAggregateModificationException("Expected version " +
        expectedVersion + " but is " + eventsForEntity.size)
    }
    val version: Int = expectedVersion + 1

    eventsForEntity += EventRow(commandId, userId, aggregateId, version, clock.instant(), event.asInstanceOf[Event[AnyRef]])
    callUpdateListeners(aggregateId, version, event)
  }

  override def getEventsForAggregate[A : TypeTag](uid: AggregateId): Seq[EventRow[Event[A]]] = {
    eventsByType
      .getOrElse(typeOf[A], throw new NoEventsForAggregateException(
        "No events found for type " + typeOf[A]))
      .getOrElse(uid, throw new NoEventsForAggregateException(
        "No events found for type " + typeOf[A] + " with uid " + uid))
      .toList.asInstanceOf[List[EventRow[Event[A]]]]
  }

  override def getEventsForAggregateFromVersion[A: TypeTag](
                  uid: AggregateId, fromVersion: Int): Seq[EventRow[Event[A]]] = {
    getEventsForAggregate(uid).drop(fromVersion)
  }

  override def getEventsForAggregateToVersion[A: TypeTag](
                  uid: AggregateId, toVersion: Int): Seq[EventRow[Event[A]]] = {
    getEventsForAggregate(uid).take(toVersion)
  }

  override def getAllAggregateIds[T: TypeTag]: Seq[AggregateId] = {
    eventsByType.get(typeOf[T]).map(_.keys.toList).getOrElse(List())
  }

  override def countAllAggregates[T: TypeTag]: Long = {
    eventsByType.get(typeOf[T]).map(_.size.toLong).getOrElse(0L)
  }

}
