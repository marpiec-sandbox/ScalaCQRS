package pl.mpieciukiewicz.user

import pl.mpieciukiewicz.user.command._
import pl.mpieciukiewicz.scalacqrs._

class UserCommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore, eventStore: EventStore)
  extends CommandBus(uidGenerator, commandStore,
    Array(new RegisterUserHandler(eventStore), new ChangeUserAddressHandler(eventStore), new DeleteUserHandler(eventStore)))