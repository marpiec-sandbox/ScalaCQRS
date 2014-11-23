package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

trait ModificationEvent[T] extends Event[T] {
  def apply(entity: T): T
}