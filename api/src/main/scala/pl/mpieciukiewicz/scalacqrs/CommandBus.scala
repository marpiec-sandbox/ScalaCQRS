package pl.mpieciukiewicz.scalacqrs

abstract class CommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore) {

  def submit[R](userId: UserId, command: Command[R]): R = {
    val newCommandId = uidGenerator.nextCommandId
    val result = handleCommand(newCommandId, command).asInstanceOf[R]
    commandStore.addCommand(newCommandId, userId, command)
    result
  }

  protected def handleCommand(commandId: CommandId, command: Command[_]): Any

}
