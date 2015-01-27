package pl.mpieciukiewicz.domain.user.command

import pl.mpieciukiewicz.domain.user.event.UserRegisteredEvent
import pl.mpieciukiewicz.scalacqrs._

class RegisterUser(userId: AggregateId, name: String) extends Command {

  def execute(commandId: CommandId, eventStore: EventStore): Unit = {
    eventStore.addCreationEvent(commandId, userId, UserRegisteredEvent(name))
  }

}