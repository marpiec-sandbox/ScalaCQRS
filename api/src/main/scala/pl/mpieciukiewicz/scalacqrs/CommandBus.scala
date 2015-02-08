package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.scalacqrs.data.UserId

abstract class CommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore, handlers: Seq[CommandHandler[_, _]]) {

  private var commandHandlers = Map[Class[Command[AnyRef]], CommandHandler[Command[AnyRef], AnyRef]]()

  handlers.foreach(registerHandler)

  private def registerHandler(handler: CommandHandler[_, _]): Unit = {
    commandHandlers += handler.commandClass.asInstanceOf[Class[Command[AnyRef]]] -> handler.asInstanceOf[CommandHandler[Command[AnyRef], AnyRef]]
  }

  def submit[R](userId: UserId, command: Command[R]): R = {
    try {
      val newCommandId = uidGenerator.nextCommandId
      val result = commandHandlers(command.getClass.asInstanceOf[Class[Command[AnyRef]]]).asInstanceOf[CommandHandler[Command[R], R]].handle(newCommandId, command)
      commandStore.addCommand(newCommandId, userId, command)
      result.asInstanceOf[R]
    } catch {
      case e:Exception => throw new Exception(commandHandlers.toString(), e)
    }
  }



}
