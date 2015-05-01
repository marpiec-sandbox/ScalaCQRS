package io.scalacqrs

import io.scalacqrs.command.{TransformCommand, Command, CommandRow}
import io.scalacqrs.data.UserId

import scala.reflect.runtime.universe._

trait CommandStore {

  def addCommand[C <: Command[_]](commandId: CommandId, userUid: UserId, command: C): Unit = {
    addTransformedCommand(commandId, userUid, transformIfNeeded(command))
  }

  protected def addTransformedCommand[C <: Command[_] : TypeTag](commandId: CommandId, userUid: UserId, command: C)

  private def transformIfNeeded(command: Command[_]) = command match {
    case transformableCommand: TransformCommand => transformableCommand.transform()
    case _ => command
  }

  def getCommandById(commandId: CommandId): CommandRow

}
