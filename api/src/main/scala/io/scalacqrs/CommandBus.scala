package io.scalacqrs

import io.scalacqrs.command.Command
import io.scalacqrs.commandhandler.CommandHandler
import io.scalacqrs.data.UserId

abstract class CommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore, handlers: Seq[CommandHandler[_ <: Command[_], _]]) {

  private var commandHandlers = Map[Class[Command[AnyRef]], CommandHandler[Command[AnyRef], AnyRef]]()

  handlers.foreach(registerHandler)

  private def registerHandler(handler: CommandHandler[_ <: Command[_], _]): Unit = {
    commandHandlers += handler.commandClass.asInstanceOf[Class[Command[AnyRef]]] -> handler.asInstanceOf[CommandHandler[Command[AnyRef], AnyRef]]
  }

  def submit[R](userId: UserId, command: Command[R]): R = {
    try {
      val newCommandId = uidGenerator.nextCommandId
      val result = commandHandlers(command.getClass.asInstanceOf[Class[Command[AnyRef]]]).asInstanceOf[CommandHandler[Command[R], R]].handle(newCommandId, userId, command)
      commandStore.addCommand(newCommandId, userId, command)
      result.asInstanceOf[R]
    } catch {
      case e:Exception => throw new Exception(e.getMessage + " .Handlers: " + commandHandlers.toString(), e)
    }
  }



}
