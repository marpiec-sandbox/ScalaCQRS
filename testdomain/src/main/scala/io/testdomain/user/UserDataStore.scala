package io.testdomain.user

import io.scalacqrs.EventStore
import io.scalacqrs.core.CoreDataStore
import io.testdomain.user.api.model.User
import io.testdomain.user.eventhandler._

class UserDataStore(eventStore: EventStore)
  extends CoreDataStore[User](eventStore,
    Array(UserRegisteredEventHandler,
      UserDuplicatedEventHandler,
      UserAddressChangedEventHandler,
      UserRemovedEventHandler))