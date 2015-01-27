package pl.mpieciukiewicz.scalacqrs

abstract class CommandBus(commandStore: CommandStore) {

  def submit(commandId: CommandId, userId: UserId, command: Command): Unit = {
    handleCommand(commandId, command)
    commandStore.addCommand(commandId, userId, command)
  }

  protected def handleCommand(commandId: CommandId, command: Command)

}
