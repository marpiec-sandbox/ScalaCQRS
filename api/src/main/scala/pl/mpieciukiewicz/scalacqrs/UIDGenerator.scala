package pl.mpieciukiewicz.scalacqrs

trait UIDGenerator {
  def nextAggregateId: AggregateId
  def nextCommandId: CommandId
}
