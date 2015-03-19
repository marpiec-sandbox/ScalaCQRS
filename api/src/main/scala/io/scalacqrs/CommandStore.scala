package io.scalacqrs

import io.scalacqrs.command.{TransformCommand, Command, CommandRow}
import io.scalacqrs.data.UserId

trait CommandStore {

  def addCommand(commandId: CommandId, userUid: UserId, command: Command[_]): Unit = {
    addTransformedCommand(commandId, userUid, transformIfNeeded(command))
  }

  protected def addTransformedCommand(commandId: CommandId, userUid: UserId, command: Command[_])

  private def transformIfNeeded(command: Command[_]) = command match {
    case transformableCommand: TransformCommand => transformableCommand.transform()
    case _ => command
  }

  def getCommandById(commandId: CommandId): CommandRow

}
