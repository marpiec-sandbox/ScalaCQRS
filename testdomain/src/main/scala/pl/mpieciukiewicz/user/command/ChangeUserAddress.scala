package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.user.event.UserAddressChangedEvent


case class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command[ChangeUserAddressResult]

case class ChangeUserAddressResult(success: Boolean)

class ChangeUserAddressHandler(eventStore: EventStore) extends CommandHandler[ChangeUserAddress, ChangeUserAddressResult](classOf[ChangeUserAddress]) {

  override def handle(commandId: CommandId, command: ChangeUserAddress): ChangeUserAddressResult = {
    eventStore.addModificationEvent(commandId, command.userId, command.expectedVersion, UserAddressChangedEvent(command.city, command.street, command.number))
    ChangeUserAddressResult(success = true)
  }
}

