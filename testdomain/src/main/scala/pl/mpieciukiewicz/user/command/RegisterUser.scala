package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.user.event.UserRegistered

case class RegisterUser(userId: AggregateId, name: String) extends Command[RegisterUserResult]

case class RegisterUserResult(success: Boolean)

class RegisterUserHandler(eventStore: EventStore) extends CommandHandler[RegisterUser, RegisterUserResult](classOf[RegisterUser]) {

  override def handle(commandId: CommandId, command: RegisterUser): RegisterUserResult = {
    eventStore.addFirstEvent(commandId, command.userId, UserRegistered(command.name))
    RegisterUserResult(success = true)
  }

}