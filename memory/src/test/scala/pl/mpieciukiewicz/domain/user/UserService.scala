package pl.mpieciukiewicz.domain.user

import pl.mpieciukiewicz.domain.user.event.{UserRemovedEvent, UserAddressChangedEvent, UserRegisteredEvent}
import pl.mpieciukiewicz.scalacqrs.{UID, EventStore}

class UserService(eventStore: EventStore) {

  def registerUser(userId: UID, newAggregateId: UID, name: String): Unit = {
    eventStore.addCreationEvent(userId, newAggregateId, UserRegisteredEvent(name))
  }

  def changeUserAddress(userId: UID, aggregateId: UID, expectedVersion: Int, city: String, street: String, number: String): Unit = {
    eventStore.addModificationEvent(userId, aggregateId, expectedVersion, UserAddressChangedEvent(city, street, number))
  }
  
  def removeUser(userId: UID, aggregateId: UID, expectedVersion: Int): Unit = {
    eventStore.addDeletionEvent(userId, aggregateId, expectedVersion, UserRemovedEvent)
  }

}
