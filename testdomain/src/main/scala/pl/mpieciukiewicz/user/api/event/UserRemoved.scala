package pl.mpieciukiewicz.user.api.event

import pl.mpieciukiewicz.scalacqrs.event.Event
import pl.mpieciukiewicz.user.api.model.User

case class UserRemoved() extends Event[User]