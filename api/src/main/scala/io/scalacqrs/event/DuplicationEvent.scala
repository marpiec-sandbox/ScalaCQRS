package io.scalacqrs.event

import io.scalacqrs.data.AggregateId

import scala.reflect.runtime.universe._
abstract class DuplicationEvent[A: TypeTag] extends Event[A] {
  val baseAggregateId: AggregateId
  val baseAggregateVersion: Int
}
