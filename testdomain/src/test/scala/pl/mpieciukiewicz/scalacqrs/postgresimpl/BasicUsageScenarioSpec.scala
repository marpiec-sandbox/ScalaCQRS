package pl.mpieciukiewicz.scalacqrs.postgresimpl

import javax.sql.DataSource

import org.apache.commons.dbcp2.BasicDataSource
import org.fest.assertions.api.Assertions.assertThat
import org.scalatest.{FeatureSpec, GivenWhenThen, BeforeAndAfter}
import pl.mpieciukiewicz.scalacqrs.data.UserId
import pl.mpieciukiewicz.scalacqrs.postgresimpl.jdbc.ConnectionPoolFactory
import pl.mpieciukiewicz.user.api.command.{UndoUserChange, ChangeUserAddress, DeleteUser, RegisterUser}
import pl.mpieciukiewicz.user.api.model.{Address, User}
import pl.mpieciukiewicz.user.{UserCommandBus, UserDataStore}


class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var eventStoreDataSource:BasicDataSource = null
  var commandStoreDataSource: BasicDataSource = null

  before {
    eventStoreDataSource = ConnectionPoolFactory.createEventStoreConnectionPool
    commandStoreDataSource = ConnectionPoolFactory.createCommandStoreConnectionPool
  }

  after {
    eventStoreDataSource.close()
    commandStoreDataSource.close()
  }

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")



      val serializer = new JsonSerializer


      val eventStore = new PostgresEventStore(eventStoreDataSource, serializer)
      val commandStore = new PostgresCommandStore(commandStoreDataSource, serializer)
      val userDataStore = new UserDataStore(eventStore)
      val uidGenerator = new PostgresUidGenerator(eventStoreDataSource)

      val userCommandBus = new UserCommandBus(uidGenerator, commandStore, eventStore)

      When("User is registered")
      val currentUserId = UserId.fromAggregateId(uidGenerator.nextAggregateId)
      val registeredUserId = uidGenerator.nextAggregateId
      val registrationResult = userCommandBus.submit(currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("Registration is successful")
      assertThat(registrationResult.success).isTrue

      Then("we can get aggregate from dataStore")
      var userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("we can get modified user from dataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))

      Then("also we can get previous version of user from dataStore")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 1)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))

      When("User is removed")
      userCommandBus.submit(currentUserId, new DeleteUser(registeredUserId, 2))

      Then("Will get empty aggregate from dataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.isEmpty).isTrue

      Then("Also we can get previous version of user, before deletion")
      userAggregate = userDataStore.getAggregateByVersion(registeredUserId, 2)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))

      When("Deletion is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 3))

      Then("We can get user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.version).isEqualTo(4)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))


      When("Address change is undone")
      userCommandBus.submit(currentUserId, new UndoUserChange(registeredUserId, 4))

      Then("We can get user from userDataStore without address")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.version).isEqualTo(5)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userCommandBus.submit(currentUserId, new ChangeUserAddress(registeredUserId, 5, "Warsaw", "Suburb", "1"))

      Then("we can get modified user from userDataStore")
      userAggregate = userDataStore.getAggregate(registeredUserId)
      assertThat(userAggregate.get.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Suburb", "1"))))


    }

  }


}
