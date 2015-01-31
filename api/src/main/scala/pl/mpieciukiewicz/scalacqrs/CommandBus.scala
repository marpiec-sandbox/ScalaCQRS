package pl.mpieciukiewicz.scalacqrs

abstract class CommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore) {

  private var handlers = Map[Class[Command[AnyRef]], CommandHandler[Command[AnyRef], AnyRef]]()

  def submit[R](userId: UserId, command: Command[R]): R = {
    try {
      val newCommandId = uidGenerator.nextCommandId
      val result = handlers(command.getClass.asInstanceOf[Class[Command[AnyRef]]]).asInstanceOf[CommandHandler[Command[R], AnyRef]].handle(newCommandId, command)
      commandStore.addCommand(newCommandId, userId, command)
      result.asInstanceOf[R]
    } catch {
      case e:Exception => throw new Exception(handlers.toString())
    }
  }

  protected def registerHandler(handler: CommandHandler[_ <: AnyRef, _ <: AnyRef]): Unit = {
    handlers += handler.commandType.asInstanceOf[Class[Command[AnyRef]]] -> handler.asInstanceOf[CommandHandler[Command[AnyRef], AnyRef]]
  }

}
