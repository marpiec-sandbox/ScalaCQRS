package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.time.Clock

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.command.{Command, TransformCommand, CommandRow}
import pl.mpieciukiewicz.scalacqrs.data.UserId
import pl.mpieciukiewicz.scalacqrs.exception.CommandAlreadyExistsException

import scala.collection.mutable

class MemoryCommandStore(clock: Clock) extends CommandStore {

  private val commands: mutable.Map[CommandId, CommandRow] = mutable.Map()

  override def addCommand(commandId: CommandId, userId: UserId, command: Command[_]): Unit = {
    if (commands.contains(commandId)) {
      throw new CommandAlreadyExistsException("Command already exists for id " + commandId)
    } else {
      commands += commandId -> CommandRow(commandId, userId, clock.instant(), transformIfNeeded(command))
    }
  }

  def transformIfNeeded(command: Command[_]) = command match {
      case transformableCommand: TransformCommand => transformableCommand.transform()
      case _ => command
    }

  override def getCommandById(commandId: CommandId) = commands.getOrElse(commandId, throw new IllegalArgumentException("No command exists with id " + commandId))
}
