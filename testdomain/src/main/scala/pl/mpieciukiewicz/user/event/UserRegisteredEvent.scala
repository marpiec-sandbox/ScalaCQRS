package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.user.entity.User
import pl.mpieciukiewicz.scalacqrs.CreationEvent

case class UserRegisteredEvent(name: String) extends CreationEvent[User] {

  override def applyEvent(): User = User(name, None)

}
