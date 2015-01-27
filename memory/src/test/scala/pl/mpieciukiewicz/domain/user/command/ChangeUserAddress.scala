package pl.mpieciukiewicz.domain.user.command

import pl.mpieciukiewicz.domain.user.event.UserAddressChangedEvent
import pl.mpieciukiewicz.scalacqrs._

class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command {

    def execute(commandId: CommandId, eventStore: EventStore): Unit = {
      eventStore.addModificationEvent(commandId, userId, expectedVersion, UserAddressChangedEvent(city, street, number))
    }

}