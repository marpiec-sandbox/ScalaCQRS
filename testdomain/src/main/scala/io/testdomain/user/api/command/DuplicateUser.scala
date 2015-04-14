package io.testdomain.user.api.command

import io.scalacqrs.command.Command
import io.scalacqrs.data.AggregateId

case class DuplicateUser(newUserId: AggregateId, baseUserId: AggregateId, baseUserVersion: Int) extends Command[DuplicateUserResult]