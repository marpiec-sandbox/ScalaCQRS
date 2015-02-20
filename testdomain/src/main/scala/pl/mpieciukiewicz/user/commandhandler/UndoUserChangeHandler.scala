package pl.mpieciukiewicz.user.commandhandler

import pl.mpieciukiewicz.scalacqrs.data.UserId
import pl.mpieciukiewicz.scalacqrs.{CommandId, EventStore}
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.user.api.command.{UndoUserChangeResult, UndoUserChange}
import pl.mpieciukiewicz.user.api.event.UserChangeUndone

class UndoUserChangeHandler(eventStore: EventStore) extends CommandHandler[UndoUserChange, UndoUserChangeResult] {
  override def handle(commandId: CommandId, userId: UserId, command: UndoUserChange): UndoUserChangeResult = {
    eventStore.addEvent(commandId, userId, command.userId, command.expectedVersion, new UserChangeUndone)
    UndoUserChangeResult(success = true)
  }
}