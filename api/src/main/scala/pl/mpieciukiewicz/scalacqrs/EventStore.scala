package pl.mpieciukiewicz.scalacqrs

import scala.collection.mutable

trait EventStore {

  private val eventListeners = mutable.Map[Class[_], mutable.ListBuffer[EventListener[_]]]()

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId]

  def addCreationEvent(commandId: CommandId, newAggregateId: AggregateId, event: CreationEvent[_])

  def addModificationEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: ModificationEvent[_])

  def addDeletionEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: DeletionEvent[_])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): Seq[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): Seq[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): Seq[EventRow[T]]

  def addEventListener(aggregateClass: Class[_], eventListener: EventListener[_]): Unit = {
    val eventListenersForType = eventListeners.getOrElseUpdate(aggregateClass, mutable.ListBuffer())
    eventListenersForType += eventListener
  }

  protected def callEventListeners[T](aggregateId: AggregateId, event: Event[T]): Unit = {
    val eventListenersForType = eventListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
    eventListenersForType.asInstanceOf[mutable.ListBuffer[EventListener[T]]].foreach(_.onEvent(aggregateId, event))
  }
}
