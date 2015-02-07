package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.scalacqrs.eventhandler.DeletionEventHandler
import pl.mpieciukiewicz.user.entity.User

object UserRemovedEventHandler extends DeletionEventHandler[User, UserRemoved]
