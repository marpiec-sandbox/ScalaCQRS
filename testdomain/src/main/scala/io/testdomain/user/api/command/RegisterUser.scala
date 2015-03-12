package io.testdomain.user.api.command

import io.scalacqrs.command.Command
import io.scalacqrs.data.AggregateId

case class RegisterUser(userId: AggregateId, name: String) extends Command[RegisterUserResult]
