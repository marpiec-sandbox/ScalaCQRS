package io.testdomain.user.eventhandler

import io.scalacqrs.eventhandler.{DuplicationEventHandler, ModificationEventHandler}
import io.testdomain.user.api.event.{UserDuplicated, UserAddressChanged}
import io.testdomain.user.api.model.{Address, User}

object UserDuplicatedEventHandler extends DuplicationEventHandler[User, UserDuplicated] {

  override def handleEvent(aggregate: User, event: UserDuplicated): User = {
    aggregate
  }
}
