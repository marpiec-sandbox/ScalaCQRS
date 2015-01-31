package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.user.event.UserAddressChangedEvent
import pl.mpieciukiewicz.scalacqrs._


case class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command[ChangeUserAddressResult]

case class ChangeUserAddressResult(success: Boolean)

class ChangeUserAddressHandler(eventStore: EventStore) extends CommandHandler[ChangeUserAddress, ChangeUserAddressResult] {

  override def handle(commandId: CommandId, command: ChangeUserAddress): ChangeUserAddressResult = {
    eventStore.addModificationEvent(commandId, command.userId, command.expectedVersion, UserAddressChangedEvent(command.city, command.street, command.number))
    ChangeUserAddressResult(success = true)
  }

  override def commandType: Class[ChangeUserAddress] = classOf[ChangeUserAddress]
}

