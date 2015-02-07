package pl.mpieciukiewicz.scalacqrs.eventhandler

import pl.mpieciukiewicz.scalacqrs.event.Event


abstract class ModificationEventHandler[A, E <: Event[A]] extends EventHandler[A, E] {
  def handleEvent(aggregate: A, event: E): A
}