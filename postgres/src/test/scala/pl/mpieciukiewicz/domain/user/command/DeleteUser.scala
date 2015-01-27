package pl.mpieciukiewicz.domain.user.command

import pl.mpieciukiewicz.domain.user.event.UserRemovedEvent
import pl.mpieciukiewicz.scalacqrs._

class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command {

  def execute(commandId: CommandId, eventStore: EventStore): Unit = {
    eventStore.addDeletionEvent(commandId, userId, expectedVersion, UserRemovedEvent)
  }
  
}