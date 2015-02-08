package pl.mpieciukiewicz.user

import pl.mpieciukiewicz.scalacqrs.EventStore
import pl.mpieciukiewicz.scalacqrs.core.CoreDataStore
import pl.mpieciukiewicz.user.api.model.User
import pl.mpieciukiewicz.user.eventhandler._

class UserDataStore(eventStore: EventStore)
  extends CoreDataStore[User](eventStore, classOf[User],
    Array(UserRegisteredEventHandler, UserAddressChangedEventHandler, UserRemovedEventHandler))