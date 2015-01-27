package pl.mpieciukiewicz.scalacqrs

import org.fest.assertions.api.Assertions.assertThat
import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.domain.user.command.{DeleteUser, ChangeUserAddress, RegisterUser}
import pl.mpieciukiewicz.domain.user.UserCommandBus
import pl.mpieciukiewicz.domain.user.entity.{Address, User}
import pl.mpieciukiewicz.jdbs.ConnectionPoolFactory
import pl.mpieciukiewicz.mpjsons.MPJson
import pl.mpieciukiewicz.scalacqrs.core.CoreDataStore
import pl.mpieciukiewicz.scalacqrs.postgresimpl.{PostgresCommandStore, ObjectSerializer, PostgresEventStore, PostgresUidGenerator}

class JsonSerializer extends ObjectSerializer {

  override def toJson(obj: AnyRef): String = MPJson.serialize(obj)

  override def fromJson[E](json: String, clazz: Class[E]): E = MPJson.deserialize(json, clazz).asInstanceOf[E]
}

class BasicUsageScenarioSpec extends FeatureSpec with GivenWhenThen {

  feature("Aggregate storing and getting with event sourcing") {

    scenario("Creation and modification of user aggregate") {

      Given("EvenStore, DataStore and UID generator, and UserService")


      val eventStoreDataSource = ConnectionPoolFactory.createEventStoreConnectionPool
      val commandStoreDataSource = ConnectionPoolFactory.createCommandStoreConnectionPool
      val serializer = new JsonSerializer


      val eventStore = new PostgresEventStore(eventStoreDataSource, serializer)
      val commandStore = new PostgresCommandStore(commandStoreDataSource, serializer)
      val dataStore = new CoreDataStore(eventStore)
      val uidGenerator = new PostgresUidGenerator(eventStoreDataSource)

      val userCommand = new UserCommandBus(commandStore, eventStore)

      When("User is registered")
      val currentUserId = uidGenerator.nextUserId
      val registeredUserId = uidGenerator.nextAggregateId
      userCommand.submit(uidGenerator.nextCommandId, currentUserId, new RegisterUser(registeredUserId, "Marcin Pieciukiewicz"))

      Then("we can get aggregate from dataStore")
      var userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))


      When("Address is defined for user")
      userCommand.submit(uidGenerator.nextCommandId, currentUserId,new ChangeUserAddress(registeredUserId, 1, "Warsaw", "Center", "1"))

      Then("we can get modified user from dataStore")
      userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))

      Then("also we can get previous version of user from dataStore")
      userAggregate = dataStore.getAggregateByVersion(classOf[User], registeredUserId, 1)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", None))

      When("User is removed")
      userCommand.submit(uidGenerator.nextCommandId, currentUserId, new DeleteUser(registeredUserId, 2))

      Then("Will get empty aggregate from dataStore")
      userAggregate = dataStore.getAggregate(classOf[User], registeredUserId)
      assertThat(userAggregate.aggregateRoot.isEmpty).isTrue

      Then("Also we can get previous version of user, before deletion")
      userAggregate = dataStore.getAggregateByVersion(classOf[User], registeredUserId, 2)
      assertThat(userAggregate.aggregateRoot.get).isEqualTo(User("Marcin Pieciukiewicz", Some(Address("Warsaw", "Center", "1"))))


    }

  }


}
