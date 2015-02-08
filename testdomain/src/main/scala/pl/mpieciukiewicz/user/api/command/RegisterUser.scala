package pl.mpieciukiewicz.user.api.command

import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.data.AggregateId

case class RegisterUser(userId: AggregateId, name: String) extends Command[RegisterUserResult]
