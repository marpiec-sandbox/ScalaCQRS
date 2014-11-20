package pl.mpieciukiewicz.scalacqrs

abstract class Event[T >: Aggregate] {
  def applyEvent(aggregate: Aggregate)

  def aggregateClass: Class[T]
}
