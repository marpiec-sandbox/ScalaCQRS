package io.scalacqrs

import io.scalacqrs.data.AggregateId


case class Aggregate[T](uid: AggregateId, version: Int, aggregateRoot: Option[T])
