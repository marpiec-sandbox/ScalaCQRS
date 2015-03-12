package io.scalacqrs

import io.scalacqrs.data.AggregateId

trait UIDGenerator {
  def nextAggregateId: AggregateId
  def nextCommandId: CommandId
}
