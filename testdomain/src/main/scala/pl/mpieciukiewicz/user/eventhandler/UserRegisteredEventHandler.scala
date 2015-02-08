package pl.mpieciukiewicz.user.eventhandler

import pl.mpieciukiewicz.scalacqrs.eventhandler.CreationEventHandler
import pl.mpieciukiewicz.user.api.event.UserRegistered
import pl.mpieciukiewicz.user.api.model.User

object UserRegisteredEventHandler extends CreationEventHandler[User, UserRegistered] {

  override def handleEvent(event: UserRegistered): User = User(event.name, None)
}
