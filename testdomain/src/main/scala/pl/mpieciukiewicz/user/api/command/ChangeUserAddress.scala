package pl.mpieciukiewicz.user.api.command

import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.data.AggregateId

case class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command[ChangeUserAddressResult]
