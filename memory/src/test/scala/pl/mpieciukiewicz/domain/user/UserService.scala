package pl.mpieciukiewicz.domain.user

import pl.mpieciukiewicz.domain.user.event.{UserAddressChanged, UserRegisteredEvent}
import pl.mpieciukiewicz.scalacqrs.{UID, EventStore}

class UserService(eventStore: EventStore) {

  def registerUser(userId: UID, newAggregateId: UID, name: String): Unit = {
    eventStore.addEventForNewAggregate(userId, newAggregateId, UserRegisteredEvent(name))
  }

  def changeUserAddress(userId: UID, aggregateId: UID, expectedVersion: Int, city: String, street: String, number: String): Unit = {
    eventStore.addEventForExistingAggregate(userId, aggregateId, expectedVersion, UserAddressChanged(city, street, number))
  }

}
