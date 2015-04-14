package io.scalacqrs.eventhandler

import io.scalacqrs.event.Event


abstract class DuplicationEventHandler[A, E <: Event[A]] extends EventHandler[A, E] {
  def handleEvent(aggregate: A, event: E): A
}