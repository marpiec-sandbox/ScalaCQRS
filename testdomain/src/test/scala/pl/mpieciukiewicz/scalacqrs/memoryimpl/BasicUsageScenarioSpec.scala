package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.time.Clock

import org.fest.assertions.api.Assertions.assertThat
import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.scalacqrs.UserId
import pl.mpieciukiewicz.user.command.{DeleteUser, ChangeUserAddress, RegisterUser}
import pl.mpieciukiewicz.user.UserCommandBus
import pl.mpieciukiewicz.user.entity.{Address, User}
import pl.mpieciukiewicz.scalacqrs.core.CoreDataStore

class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen {

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")

      val eventStore = new MemoryEventStore(Clock.systemDefaultZone())
      val commandStore = new MemoryCommandStore(Clock.systemDefaultZone())
      val dataStore = new CoreDataStore(eventStore)
      val uidGenerator = new MemorySequentialUIDGenerator

      val userCommandBus = new UserCommandBus(commandStore, eventStore)

      When("User is registered")
      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val registeredUserId = uidGenerator.nextAggregateId
      userCommandBus.submit(uidGenerator.nextCommandId, currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("we can get aggregate from dataStore")
      var userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userCommandBus.submit(uidGenerator.nextCommandId, currentUserId, new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("we can get modified user from dataStore")
      userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))

      Then("also we can get previous version of user from dataStore")
      userAggregate = dataStore.getAggregateByVersion(classOf[User], registeredUserId, 1)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))

      When("User is removed")
      userCommandBus.submit(uidGenerator.nextCommandId, currentUserId, new DeleteUser(registeredUserId, 2))

      Then("Will get empty aggregate from dataStore")
      userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.isEmpty).isTrue

      Then("Also we can get previous version of user, before deletion")
      userAggregate = dataStore.getAggregateByVersion(classOf[User], registeredUserId, 2)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))



    }

  }

}
