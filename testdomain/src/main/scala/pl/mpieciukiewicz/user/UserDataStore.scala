package pl.mpieciukiewicz.user

import pl.mpieciukiewicz.scalacqrs.{EventStore, DataStore}
import pl.mpieciukiewicz.scalacqrs.core.CoreDataStore
import pl.mpieciukiewicz.user.event._

class UserDataStore(eventStore: EventStore) extends CoreDataStore(eventStore){

  registerHandler(UserRegisteredEventHandler)
  registerHandler(UserAddressChangedEventHandler)
  registerHandler(UserRemovedEventHandler)

}
