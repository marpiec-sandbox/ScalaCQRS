package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.user.event.UserRegisteredEvent
import pl.mpieciukiewicz.scalacqrs._

case class RegisterUser(userId: AggregateId, name: String) extends Command

case class RegisterUserResult(success: Boolean)

class RegisterUserHandler(eventStore: EventStore) extends CommandHandler[RegisterUser, RegisterUserResult] {
  override def handle(commandId: CommandId, command: RegisterUser): RegisterUserResult = {
    eventStore.addCreationEvent(commandId, command.userId, UserRegisteredEvent(command.name))
    RegisterUserResult(success = true)
  }
}