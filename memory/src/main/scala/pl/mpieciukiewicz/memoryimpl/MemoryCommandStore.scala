package pl.mpieciukiewicz.memoryimpl

import java.time.Clock

import pl.mpieciukiewicz.postgresimpl._
import pl.mpieciukiewicz.postgresimpl.exception.CommandAlreadyExistsException

import scala.collection.mutable

class MemoryCommandStore(clock: Clock) extends CommandStore {

  private val commands: mutable.Map[CommandId, CommandRow] = mutable.Map()

  override def addCommand(commandId: CommandId, userId: UserId, command: Command[_]): Unit = {
    if (commands.contains(commandId)) {
      throw new CommandAlreadyExistsException("Command already exists for id " + commandId)
    } else {
      commands += commandId -> CommandRow(commandId, userId, clock.instant(), command)
    }
  }

  override def getCommandById(commandId: CommandId) = commands.getOrElse(commandId, throw new IllegalArgumentException("No command exists with id " + commandId))
}
