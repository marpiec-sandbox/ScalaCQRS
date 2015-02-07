package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId


case class Aggregate[T](uid: AggregateId, version: Int, aggregateRoot: Option[T])
