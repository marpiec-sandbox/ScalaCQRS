package io.scalacqrs.eventhandler

import io.scalacqrs.event.Event

import scala.language.higherKinds

abstract class CreationEventHandler[A, E <: Event[A]] extends EventHandler[A, E] {
  def handleEvent(event: E): A
}