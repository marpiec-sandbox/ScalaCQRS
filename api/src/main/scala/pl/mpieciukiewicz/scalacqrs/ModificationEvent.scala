package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

abstract class ModificationEvent[T](aggregateType:Class[T]) extends Event[T](aggregateType) {
  def applyEvent(entity: T): T
}
