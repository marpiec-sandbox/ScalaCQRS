package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId

trait UIDGenerator {
  def nextAggregateId: AggregateId
  def nextCommandId: CommandId
}
