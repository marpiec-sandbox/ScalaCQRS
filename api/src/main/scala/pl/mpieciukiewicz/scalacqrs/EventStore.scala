package pl.mpieciukiewicz.scalacqrs

trait EventStore {

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId]

  def addCreationEvent[T](commandId: CommandId, newAggregateId: AggregateId, event: CreationEvent[T])

  def addModificationEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: ModificationEvent[T])

  def addDeletionEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: DeletionEvent[T])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): Seq[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): Seq[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): Seq[EventRow[T]]

}
