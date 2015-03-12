package io.testdomain.memoryimpl

import java.time.Clock

import io.scalacqrs.memoryimpl.{MemorySequentialUIDGenerator, MemoryCommandStore, MemoryEventStore}
import org.fest.assertions.api.Assertions._
import org.scalatest.{FeatureSpec, GivenWhenThen}
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command.{UndoUserChange, RegisterUser, DeleteUser, ChangeUserAddress}
import io.testdomain.user.api.model.{User, Address}
import io.testdomain.user.{UserDataStore, UserCommandBus}

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


      When("Deletion is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 3, 1))

      Then("We can get user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.version).isEqualTo(4)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))


      When("Address change is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 4, 1))

      Then("We can get user from userDataStore without address")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.version).isEqualTo(5)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 5, "Warsaw", "Suburb", "1"))

      Then("we can get modified user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Suburb", "1"))))

      When("New address is defined for user and two event sundone")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 6, "Warsaw", "Somewhere", "1"))
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 7, 2))

      Then("We can get correct address")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.version).isEqualTo(8)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))

    }

  }

}