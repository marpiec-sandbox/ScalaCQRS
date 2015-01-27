package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

trait ModificationEvent[T] extends Event[T] {
  def applyEvent(entity: T): T
}
