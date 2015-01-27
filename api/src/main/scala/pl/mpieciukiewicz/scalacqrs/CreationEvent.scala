package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

trait CreationEvent[T] extends Event[T] {

  def applyEvent(): T
}
