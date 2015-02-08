package pl.mpieciukiewicz.user.eventhandler

import pl.mpieciukiewicz.scalacqrs.eventhandler.ModificationEventHandler
import pl.mpieciukiewicz.user.api.event.UserAddressChanged
import pl.mpieciukiewicz.user.api.model.{Address, User}

object UserAddressChangedEventHandler extends ModificationEventHandler[User, UserAddressChanged] {

  override def handleEvent(aggregate: User, event: UserAddressChanged): User = {
    aggregate.copy(address = Some(Address(event.city, event.street, event.number)))
  }
}
