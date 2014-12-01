package pl.mpieciukiewicz.domain.user.event

import pl.mpieciukiewicz.domain.user.entity.{Address, User}
import pl.mpieciukiewicz.scalacqrs.ModificationEvent

case class UserAddressChangedEvent(city: String, street: String, number: String) extends ModificationEvent[User] {

  override def apply(entity: User): User = entity.copy(address = Some(Address(city, street, number)))

  override def entityClass: Class[User] = classOf[User]

}
