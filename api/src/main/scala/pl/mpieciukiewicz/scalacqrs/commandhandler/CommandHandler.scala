package pl.mpieciukiewicz.scalacqrs.commandhandler

import pl.mpieciukiewicz.scalacqrs.CommandId
import pl.mpieciukiewicz.scalacqrs.command.Command

abstract class CommandHandler[C <: Command[_], R](val commandType: Class[C]) {
  def handle(commandId: CommandId, command: C): R
}
