package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.user.entity.User
import pl.mpieciukiewicz.postgresimpl.CreationEvent

case class UserRegisteredEvent(name: String) extends CreationEvent[User] {

  override def applyEvent(): User = User(name, None)

  override def entityClass: Class[User] = classOf[User]
}
