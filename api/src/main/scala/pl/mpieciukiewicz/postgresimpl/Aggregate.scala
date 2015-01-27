package pl.mpieciukiewicz.postgresimpl


case class Aggregate[T](uid: AggregateId, version: Int, aggregateRoot: Option[T])
