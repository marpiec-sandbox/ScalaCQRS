package pl.mpieciukiewicz.postgresimpl

import java.time.Instant

case class CommandRow(commandId: CommandId, userId: UserId, creationTimestamp: Instant, command: Command[_])
