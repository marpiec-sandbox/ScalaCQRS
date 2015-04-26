package io.testdomain.user.commandhandler

import io.scalacqrs._
import io.scalacqrs.commandhandler.CommandHandler
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command.{ChangeUserAddress, ChangeUserAddressResult}
import io.testdomain.user.api.event.UserAddressChanged
import io.testdomain.user.api.model.User


class ChangeUserAddressHandler(eventStore: EventStore) extends CommandHandler[ChangeUserAddress, ChangeUserAddressResult] {

  override def handle(commandId: CommandId, userId: UserId, command: ChangeUserAddress): ChangeUserAddressResult = {
    eventStore.addEvent[User, UserAddressChanged](commandId, userId, command.userId, command.expectedVersion, UserAddressChanged(command.city, command.street, command.number))
    ChangeUserAddressResult(success = true)
  }
}

