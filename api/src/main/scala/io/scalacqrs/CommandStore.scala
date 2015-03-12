package io.scalacqrs

import io.scalacqrs.command.{Command, CommandRow}
import io.scalacqrs.data.UserId

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: Command[_])

  def getCommandById(commandId: CommandId): CommandRow

}
