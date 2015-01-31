package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

abstract class CreationEvent[T](aggregateType:Class[T]) extends Event[T](aggregateType) {

  def applyEvent(): T
}
