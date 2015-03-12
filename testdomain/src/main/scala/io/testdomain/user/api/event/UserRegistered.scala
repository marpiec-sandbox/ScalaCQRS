package io.testdomain.user.api.event

import io.scalacqrs.event.Event
import io.testdomain.user.api.model.User

case class UserRegistered(name: String) extends Event[User]
