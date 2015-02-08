package pl.mpieciukiewicz.user

import pl.mpieciukiewicz.scalacqrs.{EventStore, DataStore}
import pl.mpieciukiewicz.scalacqrs.core.CoreDataStore
import pl.mpieciukiewicz.user.entity.User
import pl.mpieciukiewicz.user.event._

class UserDataStore(eventStore: EventStore)
  extends CoreDataStore(eventStore, classOf[User],
    Array(UserRegisteredEventHandler, UserAddressChangedEventHandler, UserRemovedEventHandler))