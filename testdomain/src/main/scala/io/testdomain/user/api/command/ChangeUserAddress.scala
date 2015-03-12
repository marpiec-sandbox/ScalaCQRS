package io.testdomain.user.api.command

import io.scalacqrs.command.Command
import io.scalacqrs.data.AggregateId

case class ChangeUserAddress(userId: AggregateId, expectedVersion: Int, city: String, street: String, number: String) extends Command[ChangeUserAddressResult]
