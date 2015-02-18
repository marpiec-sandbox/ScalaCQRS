package pl.mpieciukiewicz.user.api.command

import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.data.AggregateId

case class UndoUserChange(userId: AggregateId, expectedVersion: Int) extends Command[UndoUserChangeResult]