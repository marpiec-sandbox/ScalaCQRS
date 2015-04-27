package io.testdomain.user.commandhandler

import io.scalacqrs._
import io.scalacqrs.commandhandler.CommandHandler
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command.{RegisterUser, RegisterUserResult}
import io.testdomain.user.api.event.{UserDuplicated, UserRegistered}
import io.testdomain.user.api.model.User


class RegisterUserHandler(eventStore: EventStore) extends CommandHandler[RegisterUser, RegisterUserResult] {

  override def handle(commandId: CommandId, userId: UserId, command: RegisterUser): RegisterUserResult = {
    eventStore.addFirstEvent(commandId, userId, command.userId, UserRegistered(command.name))
    RegisterUserResult(success = true)
  }

}