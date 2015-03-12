package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.command.{Command, CommandRow}
import pl.mpieciukiewicz.scalacqrs.data.UserId

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: Command[_])

  def getCommandById(commandId: CommandId): CommandRow

}
