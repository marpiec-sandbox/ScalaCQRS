package pl.mpieciukiewicz.scalacqrs

abstract class CreationEvent[T] extends Event[T] {

  def applyEvent(): T
}
