package pl.mpieciukiewicz.scalacqrs

abstract class CreationEvent[T](aggregateType:Class[T]) extends Event[T](aggregateType) {

  def applyEvent(): T
}
