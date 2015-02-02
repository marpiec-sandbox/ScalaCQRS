package pl.mpieciukiewicz.scalacqrs

abstract class Event[T] {
  def aggregateType:Class[T]
}
