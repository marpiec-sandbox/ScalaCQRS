package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.user.event.UserRegisteredEvent

case class RegisterUser(userId: AggregateId, name: String) extends Command[RegisterUserResult]

case class RegisterUserResult(success: Boolean)

class RegisterUserHandler(eventStore: EventStore) extends CommandHandler[RegisterUser, RegisterUserResult](classOf[RegisterUser]) {

  override def handle(commandId: CommandId, command: RegisterUser): RegisterUserResult = {
    eventStore.addCreationEvent(commandId, command.userId, UserRegisteredEvent(command.name))
    RegisterUserResult(success = true)
  }

}