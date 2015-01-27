package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.user.event.UserRegisteredEvent
import pl.mpieciukiewicz.postgresimpl._

import scala.util.{Success, Try}

class RegisterUser(userId: AggregateId, name: String) extends Command[Try[Boolean]] {

  def execute(commandId: CommandId, eventStore: EventStore): Try[Boolean] = {
    eventStore.addCreationEvent(commandId, userId, UserRegisteredEvent(name))
    Success(true)
  }

}