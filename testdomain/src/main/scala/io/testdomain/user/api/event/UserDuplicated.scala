package io.testdomain.user.api.event

import io.scalacqrs.data.AggregateId
import io.scalacqrs.event.DuplicationEvent
import io.testdomain.user.api.model.User

case class UserDuplicated(override val baseAggregateId: AggregateId,
                          override val baseAggregateVersion: Int) extends DuplicationEvent[User]
