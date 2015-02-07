package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.scalacqrs.eventhandler.CreationEventHandler
import pl.mpieciukiewicz.user.entity.User

object UserRegisteredEventHandler extends CreationEventHandler[User, UserRegistered] {

  override def handleEvent(event: UserRegistered): User = User(event.name, None)
}
