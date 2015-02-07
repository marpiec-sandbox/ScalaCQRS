package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.user.event.UserRemoved

case class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command[DeleteUserResult]

case class DeleteUserResult(success: Boolean)

class DeleteUserHandler(eventStore: EventStore) extends CommandHandler[DeleteUser, DeleteUserResult] {

  override def handle(commandId: CommandId, command: DeleteUser): DeleteUserResult = {
    eventStore.addEvent(commandId, command.userId, command.expectedVersion, UserRemoved())
    DeleteUserResult(success = true)
  }

}