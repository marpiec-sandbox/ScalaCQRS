package pl.mpieciukiewicz.scalacqrs


case class Aggregate[T](uid: UID, version: Int, aggregateRoot: Option[T])
