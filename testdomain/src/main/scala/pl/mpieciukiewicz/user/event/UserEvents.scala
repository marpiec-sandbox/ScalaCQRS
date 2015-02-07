package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.scalacqrs.event.Event
import pl.mpieciukiewicz.user.entity.User

case class UserRegistered(name: String) extends Event[User]

case class UserAddressChanged(city: String, street: String, number: String) extends Event[User]

case class UserRemoved() extends Event[User]