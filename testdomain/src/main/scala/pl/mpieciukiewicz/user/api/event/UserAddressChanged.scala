package pl.mpieciukiewicz.user.api.event

import pl.mpieciukiewicz.scalacqrs.event.Event
import pl.mpieciukiewicz.user.api.model.User

case class UserAddressChanged(city: String, street: String, number: String) extends Event[User]
