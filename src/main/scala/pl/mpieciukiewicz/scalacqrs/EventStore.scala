package pl.mpieciukiewicz.scalacqrs

trait EventStore {

  def addEventForNewAggregate(userId: UID, newAggregateId: UID, event: Event[_ <: Aggregate])

  def addEventForExistingAggregate(userId: UID, aggregateId: UID, expectedVersion: Int, event: Event[_ <: Aggregate])

  def getEventsForAggregateFromVersion(aggregateClass: Class[_ <: Aggregate], uid: UID, fromVersion: Int): List[EventRow]

  def getEventsForAggregate(aggregateClass: Class[_ <: Aggregate], uid: UID): List[EventRow]

}
