package pl.mpieciukiewicz.scalacqrs

abstract class CommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore) {

  private var handlers = Map[AnyRef, CommandHandler[_ <: Command, _]]()

  def submit[C <: Command, R](userId: UserId, command: C): R = {
    val newCommandId = uidGenerator.nextCommandId
    val result = handlers(command.getClass).asInstanceOf[CommandHandler[C, R]].handle(newCommandId, command)
    commandStore.addCommand(newCommandId, userId, command)
    result
  }

  protected def registerHandler(handler: CommandHandler[_ <: Command, _]): Unit = {
    handlers += handler.commandType -> handler
  }

}
