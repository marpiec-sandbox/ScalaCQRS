package io.scalacqrs.command

import java.time.Instant

import io.scalacqrs.CommandId
import io.scalacqrs.data.UserId

case class CommandRow(commandId: CommandId, userId: UserId, creationTimestamp: Instant, command: Command[Any])
