package pl.mpieciukiewicz.scalacqrs


case class Aggregate[T](uid: AggregateId, version: Int, aggregateRoot: Option[T])
