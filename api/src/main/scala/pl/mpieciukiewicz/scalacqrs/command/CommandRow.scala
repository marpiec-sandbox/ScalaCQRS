package pl.mpieciukiewicz.scalacqrs.command

import java.time.Instant

import pl.mpieciukiewicz.scalacqrs.CommandId
import pl.mpieciukiewicz.scalacqrs.data.UserId

case class CommandRow(commandId: CommandId, userId: UserId, creationTimestamp: Instant, command: AnyRef)
