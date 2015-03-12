package io.scalacqrs.eventhandler

import io.scalacqrs.event.Event

abstract class DeletionEventHandler[A, E <: Event[A]] extends EventHandler[A, E]