package pl.mpieciukiewicz.user.commandhandler

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.commandhandler.CommandHandler
import pl.mpieciukiewicz.user.api.command.{ChangeUserAddress, ChangeUserAddressResult}
import pl.mpieciukiewicz.user.api.event.UserAddressChanged


class ChangeUserAddressHandler(eventStore: EventStore) extends CommandHandler[ChangeUserAddress, ChangeUserAddressResult] {

  override def handle(commandId: CommandId, command: ChangeUserAddress): ChangeUserAddressResult = {
    eventStore.addEvent(commandId, command.userId, command.expectedVersion, UserAddressChanged(command.city, command.street, command.number))
    ChangeUserAddressResult(success = true)
  }
}

