package pl.mpieciukiewicz.scalacqrs

trait DataStore {
  def getAggregate[T](aggregateClass: Class[T], uid: AggregateId): Aggregate[T]

  def getAggregateByVersion[T](aggregateClass: Class[T], uid: AggregateId, version: Int): Aggregate[T]
}
