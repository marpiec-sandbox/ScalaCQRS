package io.testdomain.user.api.event

import io.scalacqrs.event.{UndoEvent}
import io.testdomain.user.api.model.User

case class UserChangeUndone(steps: Int) extends UndoEvent[User](steps)