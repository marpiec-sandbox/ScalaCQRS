package io.testdomain.user.api.command

import io.scalacqrs.command.Command
import io.scalacqrs.data.AggregateId

case class UndoUserChange(userId: AggregateId, expectedVersion: Int, stepsToUndo: Int) extends Command[UndoUserChangeResult]