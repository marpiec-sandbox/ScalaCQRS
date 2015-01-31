package pl.mpieciukiewicz.user

import pl.mpieciukiewicz.user.command.{DeleteUser, ChangeUserAddress, RegisterUser}
import pl.mpieciukiewicz.scalacqrs._

class UserCommandBus(uidGenerator: UIDGenerator, commandStore: CommandStore, eventStore: EventStore) extends CommandBus(uidGenerator, commandStore) {

  override protected def handleCommand(commandId: CommandId, command: Command[_]): Any = command match {
    case c: RegisterUser => c.execute(commandId, eventStore)
    case c: ChangeUserAddress => c.execute(commandId, eventStore)
    case c: DeleteUser => c.execute(commandId, eventStore)
  }

}
