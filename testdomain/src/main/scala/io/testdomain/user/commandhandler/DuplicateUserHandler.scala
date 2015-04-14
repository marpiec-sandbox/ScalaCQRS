package io.testdomain.user.commandhandler

import io.scalacqrs._
import io.scalacqrs.commandhandler.CommandHandler
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command.{DuplicateUser, DuplicateUserResult}
import io.testdomain.user.api.event.UserDuplicated


class DuplicateUserHandler(eventStore: EventStore) extends CommandHandler[DuplicateUser, DuplicateUserResult] {

  override def handle(commandId: CommandId, userId: UserId, command: DuplicateUser): DuplicateUserResult = {
    eventStore.addFirstEvent(commandId, userId, command.newUserId, UserDuplicated(command.baseUserId, command.baseUserVersion))
    DuplicateUserResult(success = true)
  }

}