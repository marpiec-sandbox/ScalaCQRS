package io.testdomain.user.commandhandler

import io.scalacqrs._
import io.scalacqrs.commandhandler.CommandHandler
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command.{DeleteUser, DeleteUserResult}
import io.testdomain.user.api.event.UserRemoved
import io.testdomain.user.api.model.User

class DeleteUserHandler(eventStore: EventStore) extends CommandHandler[DeleteUser, DeleteUserResult] {

  override def handle(commandId: CommandId, userId: UserId, command: DeleteUser): DeleteUserResult = {
    eventStore.addEvent[User, UserRemoved](commandId, userId, command.userId, command.expectedVersion, UserRemoved())
    DeleteUserResult(success = true)
  }

}