package pl.mpieciukiewicz.domain.user.command

import pl.mpieciukiewicz.domain.user.event.UserRemovedEvent
import pl.mpieciukiewicz.scalacqrs._

import scala.util.{Success, Try}

class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command[Try[Boolean]] {

  def execute(commandId: CommandId, eventStore: EventStore): Try[Boolean] = {
    eventStore.addDeletionEvent(commandId, userId, expectedVersion, UserRemovedEvent)
    Success(true)
  }
  
}