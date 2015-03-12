package io.testdomain.user.eventhandler

import io.scalacqrs.eventhandler.DeletionEventHandler
import io.testdomain.user.api.event.UserRemoved
import io.testdomain.user.api.model.User

object UserRemovedEventHandler extends DeletionEventHandler[User, UserRemoved]
