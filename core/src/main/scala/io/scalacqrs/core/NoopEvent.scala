package io.scalacqrs.core

import io.scalacqrs.event.Event

case class NoopEvent[A]() extends Event[A]
