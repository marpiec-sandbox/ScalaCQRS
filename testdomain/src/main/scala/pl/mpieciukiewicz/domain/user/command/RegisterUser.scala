package pl.mpieciukiewicz.domain.user.command

import pl.mpieciukiewicz.domain.user.event.UserRegisteredEvent
import pl.mpieciukiewicz.scalacqrs._

import scala.util.{Success, Try}

class RegisterUser(userId: AggregateId, name: String) extends Command[Try[Boolean]] {

  def execute(commandId: CommandId, eventStore: EventStore): Try[Boolean] = {
    eventStore.addCreationEvent(commandId, userId, UserRegisteredEvent(name))
    Success(true)
  }

}