package pl.mpieciukiewicz.scalacqrs

import java.time.Clock

import org.fest.assertions.api.Assertions.assertThat

import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.domain.user.UserService
import pl.mpieciukiewicz.domain.user.entity.{Address, User}
import pl.mpieciukiewicz.scalacqrs.memoryimpl.{MemoryUIDGenerator, MemoryDataStore, MemoryEventStore}

class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen {

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")

      val eventStore = new MemoryEventStore(Clock.systemDefaultZone())
      val dataStore = new MemoryDataStore(eventStore)
      val uidGenerator = new MemoryUIDGenerator

      val userService = new UserService(eventStore)

      When("User is registered")
      val currentUserId = uidGenerator.nextUID
      val registeredUserId = uidGenerator.nextUID
      userService.registerUser(currentUserId, registeredUserId, "Marcin Pieciukiewicz")

      Then("we can get aggregate from dataStore")
      var userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userService.changeUserAddress(currentUserId, registeredUserId, 1, "Warsaw", "Center", "1")

      Then("we can get modified user from dataStore")
      userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))

      Then("also we can get previous version of user from dataStore")
      userAggregate = dataStore.getAggregateByVersion(classOf[User], registeredUserId, 1)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))

      When("User is removed")
      userService.removeUser(currentUserId, registeredUserId, 2)

      Then("Will get empty aggregate from dataStore")
      userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.isEmpty).isTrue

      Then("Also we can get previous version of user, before deletion")
      userAggregate = dataStore.getAggregateByVersion(classOf[User], registeredUserId, 2)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))



    }

  }

}
