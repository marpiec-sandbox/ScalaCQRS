package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.AbstractEvent

trait EventStore {

  def addEventForNewAggregate[T](userId: UID, newAggregateId: UID, event: CreationEvent[T])

  def addEventForExistingAggregate[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: ModificationEvent[T])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: UID): List[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: UID, fromVersion: Int): List[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: UID, toVersion: Int): List[EventRow[T]]

}
