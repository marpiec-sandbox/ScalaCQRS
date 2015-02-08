package pl.mpieciukiewicz.user.eventhandler

import pl.mpieciukiewicz.scalacqrs.eventhandler.DeletionEventHandler
import pl.mpieciukiewicz.user.api.event.UserRemoved
import pl.mpieciukiewicz.user.api.model.User

object UserRemovedEventHandler extends DeletionEventHandler[User, UserRemoved]
