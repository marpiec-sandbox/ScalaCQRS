package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.command.CommandRow
import pl.mpieciukiewicz.scalacqrs.data.UserId

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: AnyRef)

  def getCommandById(commandId: CommandId): CommandRow

}
