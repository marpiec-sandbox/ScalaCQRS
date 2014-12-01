package pl.mpieciukiewicz.domain.user.event

import pl.mpieciukiewicz.domain.user.entity.User
import pl.mpieciukiewicz.scalacqrs.DeletionEvent

case object UserRemovedEvent extends DeletionEvent[User] {

  override def entityClass = classOf[User]

}
