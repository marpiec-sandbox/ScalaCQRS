package pl.mpieciukiewicz.postgresimpl

trait UIDGenerator {
  def nextAggregateId: AggregateId
  def nextCommandId: CommandId
  def nextUserId: UserId
}
