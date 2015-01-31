package pl.mpieciukiewicz.user.event

import pl.mpieciukiewicz.user.entity.User
import pl.mpieciukiewicz.scalacqrs.DeletionEvent

case object UserRemovedEvent extends DeletionEvent[User](classOf[User]) {
}
