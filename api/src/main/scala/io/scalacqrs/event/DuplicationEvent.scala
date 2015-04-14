package io.scalacqrs.event

import io.scalacqrs.data.AggregateId

abstract class DuplicationEvent[A]() extends Event[A] {
  val baseAggregateId: AggregateId
  val baseAggregateVersion: Int
}
