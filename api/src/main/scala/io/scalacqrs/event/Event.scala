package io.scalacqrs.event

import scala.reflect.runtime.universe._

abstract class Event[A: TypeTag] {
  /** TypeTag could be better solution */
  def aggregateType = typeOf[A]
}
