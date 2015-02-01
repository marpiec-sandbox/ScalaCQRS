package pl.mpieciukiewicz.scalacqrs

abstract class ModificationEvent[T](aggregateType:Class[T]) extends Event[T](aggregateType) {
  def applyEvent(entity: T): T
}
