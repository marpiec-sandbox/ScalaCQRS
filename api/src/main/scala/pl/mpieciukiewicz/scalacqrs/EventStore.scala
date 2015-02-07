package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.{EventRow, Event}

import scala.collection.mutable

trait EventStore {

  private val eventListeners = mutable.Map[Class[_], mutable.ListBuffer[EventListener[_]]]()

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId]

  def addFirstEvent(commandId: CommandId, newAggregateId: AggregateId, event: Event[_])

  def addEvent(commandId: CommandId, newAggregateId: AggregateId, expectedVersion: Int, event: Event[_])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): Seq[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): Seq[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): Seq[EventRow[T]]

  def addEventListener[T](aggregateClass: Class[T], eventListener: EventListener[T]): Unit = {
    val eventListenersForType = eventListeners.getOrElseUpdate(aggregateClass, mutable.ListBuffer())
    eventListenersForType += eventListener
  }

  protected def callEventListeners[T](aggregateId: AggregateId, event: Event[T]): Unit = {
    val eventListenersForType = eventListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
    eventListenersForType.asInstanceOf[mutable.ListBuffer[EventListener[T]]].foreach(_.onEvent(AggregateUpdated(aggregateId, event)))
  }
}
