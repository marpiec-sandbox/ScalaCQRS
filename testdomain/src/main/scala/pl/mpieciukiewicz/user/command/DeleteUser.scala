package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.user.event.UserRemovedEvent

case class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command[DeleteUserResult]

case class DeleteUserResult(success: Boolean)

class DeleteUserHandler(eventStore: EventStore) extends CommandHandler[DeleteUser, DeleteUserResult](classOf[DeleteUser]) {

  override def handle(commandId: CommandId, command: DeleteUser): DeleteUserResult = {
    eventStore.addDeletionEvent(commandId, command.userId, command.expectedVersion, UserRemovedEvent)
    DeleteUserResult(success = true)
  }

}