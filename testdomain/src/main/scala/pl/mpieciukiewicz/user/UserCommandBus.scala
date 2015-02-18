package pl.mpieciukiewicz.user

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.user.commandhandler._

class UserCommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore, eventStore: EventStore)
  extends CommandBus(uidGenerator, commandStore,
    Array(new RegisterUserHandler(eventStore),
          new ChangeUserAddressHandler(eventStore),
          new DeleteUserHandler(eventStore),
          new UserChangeUndoneHandler(eventStore)))