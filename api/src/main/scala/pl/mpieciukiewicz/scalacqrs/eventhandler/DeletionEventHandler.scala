package pl.mpieciukiewicz.scalacqrs.eventhandler

import pl.mpieciukiewicz.scalacqrs.event.Event

abstract class DeletionEventHandler[A, E <: Event[A]] extends EventHandler[A, E]