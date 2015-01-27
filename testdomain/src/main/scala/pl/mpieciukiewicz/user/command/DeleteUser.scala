package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.user.event.UserRemovedEvent
import pl.mpieciukiewicz.postgresimpl._

import scala.util.{Success, Try}

class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command[Try[Boolean]] {

  def execute(commandId: CommandId, eventStore: EventStore): Try[Boolean] = {
    eventStore.addDeletionEvent(commandId, userId, expectedVersion, UserRemovedEvent)
    Success(true)
  }
  
}