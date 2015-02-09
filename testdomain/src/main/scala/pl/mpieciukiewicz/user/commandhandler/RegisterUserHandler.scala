package pl.mpieciukiewicz.user.commandhandler

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.scalacqrs.data.UserId
import pl.mpieciukiewicz.user.api.command.{RegisterUser, RegisterUserResult}
import pl.mpieciukiewicz.user.api.event.UserRegistered


class RegisterUserHandler(eventStore: EventStore) extends CommandHandler[RegisterUser, RegisterUserResult] {

  override def handle(commandId: CommandId, userId: UserId, command: RegisterUser): RegisterUserResult = {
    eventStore.addFirstEvent(commandId, userId, command.userId, UserRegistered(command.name))
    RegisterUserResult(success = true)
  }

}