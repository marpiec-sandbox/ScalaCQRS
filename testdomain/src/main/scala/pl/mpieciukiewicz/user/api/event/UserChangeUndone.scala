package pl.mpieciukiewicz.user.api.event

import pl.mpieciukiewicz.scalacqrs.event.{UndoEvent}
import pl.mpieciukiewicz.user.api.model.User

case class UserChangeUndone() extends UndoEvent[User](1)