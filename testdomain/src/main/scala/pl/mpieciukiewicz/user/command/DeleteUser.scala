package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs
import pl.mpieciukiewicz.user.event.{UserAddressChangedEvent, UserRemovedEvent}
import pl.mpieciukiewicz.scalacqrs._

import scala.util.{Success, Try}

case class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command

case class DeleteUserResult(success: Boolean)

class DeleteUserHandler(eventStore: EventStore) extends CommandHandler[DeleteUser, DeleteUserResult]{

  override def handle(commandId: CommandId, command: DeleteUser): DeleteUserResult = {
    eventStore.addDeletionEvent(commandId, command.userId, command.expectedVersion, UserRemovedEvent)
    DeleteUserResult(success = true)
  }
}