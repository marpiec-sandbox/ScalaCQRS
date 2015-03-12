package io.testdomain.user.eventhandler

import io.scalacqrs.eventhandler.ModificationEventHandler
import io.testdomain.user.api.event.UserAddressChanged
import io.testdomain.user.api.model.{Address, User}

object UserAddressChangedEventHandler extends ModificationEventHandler[User, UserAddressChanged] {

  override def handleEvent(aggregate: User, event: UserAddressChanged): User = {
    aggregate.copy(address = Some(Address(event.city, event.street, event.number)))
  }
}
