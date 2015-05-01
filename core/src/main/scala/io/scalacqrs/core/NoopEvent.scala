package io.scalacqrs.core

import io.scalacqrs.event.Event

import scala.reflect.runtime.universe._

case class NoopEvent[A: TypeTag]() extends Event[A]
