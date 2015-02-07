package pl.mpieciukiewicz.scalacqrs.eventhandler

import pl.mpieciukiewicz.scalacqrs.event.Event

import scala.language.higherKinds

abstract class CreationEventHandler[A, E <: Event[A]] extends EventHandler[A, E] {
  def handleEvent(event: E): A
}