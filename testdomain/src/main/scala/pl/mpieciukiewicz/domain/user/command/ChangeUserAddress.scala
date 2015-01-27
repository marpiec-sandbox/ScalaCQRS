package pl.mpieciukiewicz.domain.user.command

import pl.mpieciukiewicz.domain.user.event.UserAddressChangedEvent
import pl.mpieciukiewicz.scalacqrs._

import scala.util.{Success, Try}

class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command[Try[Boolean]] {

    def execute(commandId: CommandId, eventStore: EventStore): Try[Boolean] = {
      eventStore.addModificationEvent(commandId, userId, expectedVersion, UserAddressChangedEvent(city, street, number))
      Success(true)
    }

}