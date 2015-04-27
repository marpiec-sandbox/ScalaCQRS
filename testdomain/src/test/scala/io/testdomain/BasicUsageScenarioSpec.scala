package io.testdomain

import io.scalacqrs._
import io.scalacqrs.event.Event
import io.testdomain.user.api.event.{UserAddressChanged, UserRemoved}
import org.scalatest.{FeatureSpec, GivenWhenThen, MustMatchers}
import MustMatchers._
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command._
import io.testdomain.user.api.model.{Address, User}
import io.testdomain.user.{UserCommandBus, UserDataStore}

abstract class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen {

/** Can be done more elegant via trait and adding it to scenerios
  * event store should be reseted becouse of listners */
  var eventStore: EventStore
  var commandStore: CommandStore
  var uidGenerator: UIDGenerator

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")

      val userDataStore: UserDataStore = new UserDataStore(eventStore)
      val userCommandBus: UserCommandBus = new UserCommandBus(uidGenerator, commandStore, eventStore)

      When("User is registered")
      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val registeredUserId = uidGenerator.nextAggregateId
      val registrationResult = userCommandBus.submit(
        currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("we can get aggregate from userDataStore")
      registrationResult.success mustBe true

      var userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)

      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("we can get modified user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

      Then("also we can get previous version of user from userDataStore")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 1)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)

      Then("other way gives same object")
      val other = userDataStore.getAggregateByVersionAndApplyEventToIt(
        registeredUserId, 1, UserAddressChanged("Warsaw", "Center", "1"))
      other.get.aggregateRoot.get mustBe
        User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

      When("User is removed")
      val deleteEvent = new DeleteUser(registeredUserId, 2)
      userCommandBus.submit(currentUserId, deleteEvent)

      Then("Will get empty aggregate from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot mustBe empty

      Then("Also we can get previous version of user, before deletion")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 2)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

      Then("Will get empty aggregate from userDataStore with applied event")
      userAggregate = userDataStore.getAggregateByVersionAndApplyEventToIt(registeredUserId, 2, UserRemoved())
      userAggregate.get.aggregateRoot mustBe empty

      When("Deletion is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 3, 1))

      Then("We can get user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.version mustBe 4
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

      When("Address change is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 4, 1))

      Then("We can get user from userDataStore without address")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.version mustBe 5
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)


      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 5, "Warsaw", "Suburb", "1"))

      Then("we can get modified user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Suburb", "1")))

      When("New address is defined for user and two event sundone")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 6, "Warsaw", "Somewhere", "1"))
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 7, 2))

      Then("We can get correct address")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.version mustBe 8
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)

    }

    scenario("StateListener: Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")

      val userDataStore: UserDataStore = new UserDataStore(eventStore)
      val userCommandBus: UserCommandBus = new UserCommandBus(uidGenerator, commandStore, eventStore)
      var lastStateUpdate: AggregateState[Event[User]] = null

      When("User is registered")
      eventStore.addStateChangedListener[User](
        (a: AggregateState[Event[User]]) => lastStateUpdate = a)

      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val registeredUserId = uidGenerator.nextAggregateId
      val registrationResult = userCommandBus.submit(
        currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("We have right user state")
      lastStateUpdate.aggregate.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)

      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("We have right user state")
      lastStateUpdate.aggregate.aggregateRoot.get mustBe User(
        "Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

      When("User is removed")
      val deleteEvent = new DeleteUser(registeredUserId, 2)
      userCommandBus.submit(currentUserId, deleteEvent)

      When("Deletion is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 3, 1))

      When("Address change is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 4, 1))

      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 5, "Warsaw", "Suburb", "1"))

      Then("We have right user state")
      lastStateUpdate.aggregate.aggregateRoot.get mustBe User(
        "Marcin Pieciukiewicz", Some(Address("Warsaw", "Suburb", "1")))

      When("New address is defined for user and two event sundone")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 6, "Warsaw", "Somewhere", "1"))
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 7, 2))

      Then("We have right user state")
      lastStateUpdate.aggregate.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)
    }

    scenario("Creating new aggregate based on other aggregate of the same type") {

      Given("EvenStore, DataStore and UID generator, and UserService")

      val userDataStore: UserDataStore = new UserDataStore(eventStore)
      val userCommandBus: UserCommandBus = new UserCommandBus(uidGenerator, commandStore, eventStore)
      var lastStateUpdate: AggregateState[Event[User]] = null

      When("User is registered")

      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val firstUserId = uidGenerator.nextAggregateId
      userCommandBus.submit(currentUserId, new RegisterUser(firstUserId, "Marcin Pieciukiewicz"))

      When("Second user is created based on the previous user")

      val secondUserId = uidGenerator.nextAggregateId
      userCommandBus.submit(currentUserId, new DuplicateUser(secondUserId, firstUserId, 1))

      When("First and Second users' addresses are modified differently")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(firstUserId, 1, "Warsaw", "Center", "1"))
      userCommandBus.submit(currentUserId, new ChangeUserAddress(secondUserId, 1, "Cracow", "Main Street", "5"))

      Then("We can get both versions of user")
      val firstUser = userDataStore.getAggregate(firstUserId).get
      val secondUser = userDataStore.getAggregate(secondUserId).get

      firstUser.version mustBe 2
      firstUser.aggregateRoot.get.address.get mustBe Address("Warsaw", "Center", "1")

      secondUser.version mustBe 2
      secondUser.aggregateRoot.get.address.get mustBe Address("Cracow", "Main Street", "5")

    }
  }

}
