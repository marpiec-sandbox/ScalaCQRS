package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.user.entity.{Address, User}
import pl.mpieciukiewicz.scalacqrs.ModificationEvent

case class UserAddressChangedEvent(city: String, street: String, number: String) extends ModificationEvent[User] {

  override def applyEvent(entity: User): User = entity.copy(address = Some(Address(city, street, number)))

  override def aggregateType: Class[User] = classOf[User]
}
