package pl.mpieciukiewicz.scalacqrs

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: AnyRef)

  def getCommandById(commandId: CommandId): CommandRow

}
