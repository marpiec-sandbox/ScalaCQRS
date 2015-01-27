package pl.mpieciukiewicz.postgresimpl

import pl.mpieciukiewicz.postgresimpl.internal.Event

trait CreationEvent[T] extends Event[T] {

  def applyEvent(): T
}
