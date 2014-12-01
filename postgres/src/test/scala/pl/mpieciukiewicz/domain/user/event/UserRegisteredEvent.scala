package pl.mpieciukiewicz.domain.user.event

import pl.mpieciukiewicz.domain.user.entity.User
import pl.mpieciukiewicz.scalacqrs.CreationEvent

case class UserRegisteredEvent(name: String) extends CreationEvent[User] {

  override def apply(): User = User(name, None)

  override def entityClass: Class[User] = classOf[User]
}
