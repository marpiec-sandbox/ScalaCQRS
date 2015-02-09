package pl.mpieciukiewicz.user.commandhandler

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.scalacqrs.data.UserId
import pl.mpieciukiewicz.user.api.command.{DeleteUser, DeleteUserResult}
import pl.mpieciukiewicz.user.api.event.UserRemoved

class DeleteUserHandler(eventStore: EventStore) extends CommandHandler[DeleteUser, DeleteUserResult] {

  override def handle(commandId: CommandId, userId: UserId, command: DeleteUser): DeleteUserResult = {
    eventStore.addEvent(commandId, userId, command.userId, command.expectedVersion, UserRemoved())
    DeleteUserResult(success = true)
  }

}