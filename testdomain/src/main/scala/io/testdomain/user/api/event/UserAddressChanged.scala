package io.testdomain.user.api.event

import io.scalacqrs.event.Event
import io.testdomain.user.api.model.User

case class UserAddressChanged(city: String, street: String, number: String) extends Event[User]
