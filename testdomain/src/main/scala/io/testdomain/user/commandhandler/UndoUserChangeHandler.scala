package io.testdomain.user.commandhandler

import io.scalacqrs.data.UserId
import io.scalacqrs.{CommandId, EventStore}
import io.scalacqrs.commandhandler.CommandHandler
import io.testdomain.user.api.command.{UndoUserChangeResult, UndoUserChange}
import io.testdomain.user.api.event.{UserRegistered, UserChangeUndone}
import io.testdomain.user.api.model.User

class UndoUserChangeHandler(eventStore: EventStore) extends CommandHandler[UndoUserChange, UndoUserChangeResult] {
  override def handle(commandId: CommandId, userId: UserId, command: UndoUserChange): UndoUserChangeResult = {
    eventStore.addEvent(commandId, userId, command.userId, command.expectedVersion, new UserChangeUndone(command.stepsToUndo))
    UndoUserChangeResult(success = true)
  }
}