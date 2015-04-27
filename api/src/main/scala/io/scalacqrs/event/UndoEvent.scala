package io.scalacqrs.event

import scala.reflect.runtime.universe._

abstract class UndoEvent[A: TypeTag] extends Event[A] {
  val eventsCount: Int
}
