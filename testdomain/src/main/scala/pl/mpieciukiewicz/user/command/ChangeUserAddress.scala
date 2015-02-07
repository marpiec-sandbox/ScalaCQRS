package pl.mpieciukiewicz.user.command

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.user.event.UserAddressChanged


case class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command[ChangeUserAddressResult]

case class ChangeUserAddressResult(success: Boolean)

class ChangeUserAddressHandler(eventStore: EventStore) extends CommandHandler[ChangeUserAddress, ChangeUserAddressResult] {

  override def handle(commandId: CommandId, command: ChangeUserAddress): ChangeUserAddressResult = {
    eventStore.addEvent(commandId, command.userId, command.expectedVersion, UserAddressChanged(command.city, command.street, command.number))
    ChangeUserAddressResult(success = true)
  }
}

