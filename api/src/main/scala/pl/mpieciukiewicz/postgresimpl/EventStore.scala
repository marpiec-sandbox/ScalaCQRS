package pl.mpieciukiewicz.postgresimpl

import pl.mpieciukiewicz.postgresimpl.internal.Event

trait EventStore {

  def addCreationEvent[T](commandId: CommandId, newAggregateId: AggregateId, event: CreationEvent[T])

  def addModificationEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: ModificationEvent[T])

  def addDeletionEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: DeletionEvent[T])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): List[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): List[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): List[EventRow[T]]

}
