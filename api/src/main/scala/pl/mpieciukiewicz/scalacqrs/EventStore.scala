package pl.mpieciukiewicz.scalacqrs

trait EventStore {

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId]

  def addCreationEvent(commandId: CommandId, newAggregateId: AggregateId, event: CreationEvent[_])

  def addModificationEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: ModificationEvent[_])

  def addDeletionEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: DeletionEvent[_])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): Seq[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): Seq[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): Seq[EventRow[T]]

}
