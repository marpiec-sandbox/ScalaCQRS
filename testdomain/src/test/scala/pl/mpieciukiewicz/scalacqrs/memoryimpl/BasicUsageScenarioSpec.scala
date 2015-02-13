package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.time.Clock

import org.fest.assertions.api.Assertions.assertThat
import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.scalacqrs.data.UserId
import pl.mpieciukiewicz.user.api.command.{RegisterUser, DeleteUser, ChangeUserAddress}
import pl.mpieciukiewicz.user.api.model.{User, Address}
import pl.mpieciukiewicz.user.{UserDataStore, UserCommandBus}
import pl.mpieciukiewicz.scalacqrs.core.CoreDataStore
import pl.mpieciukiewicz.user.eventhandler.{UserAddressChangedEventHandler, UserRegisteredEventHandler}

class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen {

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")

      val eventStore = new MemoryEventStore(Clock.systemDefaultZone())
      val commandStore = new MemoryCommandStore(Clock.systemDefaultZone())
      val userDataStore = new UserDataStore(eventStore)

      val uidGenerator = new MemorySequentialUIDGenerator

      val userCommandBus = new UserCommandBus(uidGenerator, commandStore, eventStore)

      When("User is registered")
      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val registeredUserId = uidGenerator.nextAggregateId
      val registrationResult = userCommandBus.submit(currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("we can get aggregate from userDataStore")
      assertThat(registrationResult.success).isTrue

      var userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("we can get modified user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))

      Then("also we can get previous version of user from userDataStore")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 1)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))

      When("User is removed")
      userCommandBus.submit(currentUserId, new DeleteUser(registeredUserId, 2))

      Then("Will get empty aggregate from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.isEmpty).isTrue

      Then("Also we can get previous version of user, before deletion")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 2)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))



    }

  }

}
