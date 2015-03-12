package io.testdomain.user.eventhandler

import io.scalacqrs.eventhandler.CreationEventHandler
import io.testdomain.user.api.event.UserRegistered
import io.testdomain.user.api.model.User

object UserRegisteredEventHandler extends CreationEventHandler[User, UserRegistered] {

  override def handleEvent(event: UserRegistered): User = User(event.name, None)
}
