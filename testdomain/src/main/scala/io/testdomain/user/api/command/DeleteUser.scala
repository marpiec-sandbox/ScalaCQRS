package io.testdomain.user.api.command

import io.scalacqrs.command.Command
import io.scalacqrs.data.AggregateId

case class DeleteUser(userId: AggregateId, expectedVersion: Int) extends Command[DeleteUserResult]
