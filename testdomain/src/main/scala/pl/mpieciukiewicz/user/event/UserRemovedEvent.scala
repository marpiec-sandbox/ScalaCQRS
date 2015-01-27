package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.user.entity.User
import pl.mpieciukiewicz.postgresimpl.DeletionEvent

case object UserRemovedEvent extends DeletionEvent[User] {

  override def entityClass = classOf[User]

}
