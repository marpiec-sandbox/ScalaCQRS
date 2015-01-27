package pl.mpieciukiewicz.postgresimpl

import pl.mpieciukiewicz.postgresimpl.internal.Event

trait ModificationEvent[T] extends Event[T] {
  def applyEvent(entity: T): T
}
