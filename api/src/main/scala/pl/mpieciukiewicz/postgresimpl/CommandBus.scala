package pl.mpieciukiewicz.postgresimpl

abstract class CommandBus(commandStore: CommandStore) {

  def submit[R](commandId: CommandId, userId: UserId, command: Command[R]): R = {
    val result = handleCommand(commandId, command).asInstanceOf[R]
    commandStore.addCommand(commandId, userId, command)
    result
  }

  protected def handleCommand(commandId: CommandId, command: Command[_]): Any

}
