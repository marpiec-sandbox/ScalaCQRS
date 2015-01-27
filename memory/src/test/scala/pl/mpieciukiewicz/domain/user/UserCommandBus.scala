package pl.mpieciukiewicz.domain.user

import pl.mpieciukiewicz.domain.user.command.{DeleteUser, ChangeUserAddress, RegisterUser}
import pl.mpieciukiewicz.scalacqrs._

class UserCommandBus(commandStore: CommandStore, eventStore: EventStore) extends CommandBus(commandStore) {

  override protected def handleCommand(commandId: CommandId, command: Command) = command match {
    case c: RegisterUser => c.execute(commandId, eventStore)
    case c: ChangeUserAddress => c.execute(commandId, eventStore)
    case c: DeleteUser => c.execute(commandId, eventStore)
  }
}
