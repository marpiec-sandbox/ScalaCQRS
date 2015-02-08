package pl.mpieciukiewicz.user.commandhandler

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.user.api.command.{RegisterUser, RegisterUserResult}
import pl.mpieciukiewicz.user.api.event.UserRegistered


class RegisterUserHandler(eventStore: EventStore) extends CommandHandler[RegisterUser, RegisterUserResult] {

  override def handle(commandId: CommandId, command: RegisterUser): RegisterUserResult = {
    eventStore.addFirstEvent(commandId, command.userId, UserRegistered(command.name))
    RegisterUserResult(success = true)
  }

}