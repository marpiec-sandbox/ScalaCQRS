package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

trait EventStore {

  def addCreationEvent[T](userId: UID, newAggregateId: UID, event: CreationEvent[T])

  def addModificationEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: ModificationEvent[T])

  def addDeletionEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: DeletionEvent[T])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: UID): List[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: UID, fromVersion: Int): List[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: UID, toVersion: Int): List[EventRow[T]]

}
