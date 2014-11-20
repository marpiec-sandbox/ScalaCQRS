package pl.mpieciukiewicz.scalacqrs

trait DataStore {
  def getAggregate[T <: Aggregate](aggregateClass: Class[T], uid: UID): T

  def getAggregateByVersion[T <: Aggregate](aggregateClass: Class[T], uid: UID, version: Int): T
}
