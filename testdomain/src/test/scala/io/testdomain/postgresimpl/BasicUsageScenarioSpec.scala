package io.testdomain.postgresimpl

import io.scalacqrs.postgresimpl.{PostgresUidGenerator, PostgresCommandStore, PostgresEventStore}
import io.testdomain.postgresimpl.jdbc.ConnectionPoolFactory
import org.apache.commons.dbcp2.BasicDataSource
import org.scalatest.{FeatureSpec, GivenWhenThen, BeforeAndAfter, MustMatchers}
import MustMatchers._
import io.scalacqrs.data.UserId
import io.testdomain.user.api.command.{UndoUserChange, ChangeUserAddress, DeleteUser, RegisterUser}
import io.testdomain.user.api.model.{User, Address}
import io.testdomain.user.{UserCommandBus, UserDataStore}


class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var dataSource:BasicDataSource = null
  before {
    dataSource = ConnectionPoolFactory.createSampleConnectionPool
  }

  after {
    dataSource.close()
  }

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")



      val serializer = new JsonSerializer


      val eventStore = new PostgresEventStore(dataSource, serializer)
      val commandStore = new PostgresCommandStore(dataSource, serializer)
      val userDataStore = new UserDataStore(eventStore)
      val uidGenerator = new PostgresUidGenerator(dataSource)

      val userCommandBus = new UserCommandBus(uidGenerator, commandStore, eventStore)

      When("User is registered")
      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val registeredUserId = uidGenerator.nextAggregateId
      val registrationResult = userCommandBus.submit(currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("Registration is successful")
      registrationResult.success mustBe true

      Then("we can get aggregate from dataStore")
      var userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)


      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("we can get modified user from dataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

      Then("also we can get previous version of user from dataStore")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 1)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", None)

      When("User is removed")
      userCommandBus.submit(currentUserId, new DeleteUser(registeredUserId, 2))

      Then("Will get empty aggregate from dataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      userAggregate.get.aggregateRoot mustBe empty

      Then("Also we can get previous version of user, before deletion")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 2)
      userAggregate.get.aggregateRoot.get mustBe User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1")))

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

  }


}
