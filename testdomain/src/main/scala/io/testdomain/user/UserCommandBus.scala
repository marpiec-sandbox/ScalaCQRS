package io.testdomain.user

import io.scalacqrs._
import io.testdomain.user.commandhandler._

class UserCommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore, eventStore: EventStore)
  extends CommandBus(uidGenerator, commandStore,
    Array(new RegisterUserHandler(eventStore),
          new ChangeUserAddressHandler(eventStore),
          new DeleteUserHandler(eventStore),
          new UndoUserChangeHandler(eventStore)))