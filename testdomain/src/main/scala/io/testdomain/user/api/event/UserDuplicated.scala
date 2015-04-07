package io.testdomain.user.api.event

import io.scalacqrs.data.AggregateId
import io.scalacqrs.event.Event
import io.testdomain.user.api.model.User

case class UserDuplicated(baseUserId: AggregateId, baseUserVersion: Int) extends Event[User]
