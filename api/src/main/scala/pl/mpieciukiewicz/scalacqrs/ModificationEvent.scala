package pl.mpieciukiewicz.scalacqrs

abstract class ModificationEvent[T] extends Event[T] {
  def applyEvent(entity: T): T
}
