package pl.mpieciukiewicz.scalacqrs

trait DataStore {

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]):Seq[AggregateId]

  def getAggregate[T](aggregateClass: Class[T], uid: AggregateId): Aggregate[T]

  def getAggregateByVersion[T](aggregateClass: Class[T], uid: AggregateId, version: Int): Aggregate[T]
}
