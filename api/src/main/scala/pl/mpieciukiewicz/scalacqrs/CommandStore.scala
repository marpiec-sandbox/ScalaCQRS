package pl.mpieciukiewicz.scalacqrs

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: Command)

  def getCommandById(commandId: CommandId): CommandRow

}
