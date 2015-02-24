package pl.mpieciukiewicz.scalacqrs.core

import pl.mpieciukiewicz.scalacqrs.event.Event

case class NoopEvent[A]() extends Event[A]
