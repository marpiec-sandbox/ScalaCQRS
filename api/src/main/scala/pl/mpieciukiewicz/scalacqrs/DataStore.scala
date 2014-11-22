package pl.mpieciukiewicz.scalacqrs

trait DataStore {
  def getAggregate[T](aggregateClass: Class[T], uid: UID): Aggregate[T]

  def getAggregateByVersion[T](aggregateClass: Class[T], uid: UID, version: Int): Aggregate[T]
}
