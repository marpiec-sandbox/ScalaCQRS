package pl.mpieciukiewicz.postgresimpl

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: Command[_])

  def getCommandById(commandId: CommandId): CommandRow

}
