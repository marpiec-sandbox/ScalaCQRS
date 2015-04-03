package io.testdomain.postgresimpl

import io.scalacqrs.postgresimpl.{PostgresCommandStore, PostgresEventStore, PostgresUidGenerator}
import io.scalacqrs.{CommandStore, EventStore, UIDGenerator}
import io.testdomain.BasicUsageScenarioSpec
import io.testdomain.postgresimpl.jdbc.ConnectionPoolFactory
import org.apache.commons.dbcp2.BasicDataSource
import org.scalatest.BeforeAndAfter

class PostgresUsageScenarioSpec extends BasicUsageScenarioSpec with BeforeAndAfter {

  val serializer = new JsonSerializer

  var eventStore: EventStore = null
  var commandStore: CommandStore = null
  var uidGenerator: UIDGenerator = null

  var dataSource:BasicDataSource = null
  before {
    dataSource = ConnectionPoolFactory.createSampleConnectionPool

    eventStore = new PostgresEventStore(dataSource, serializer)
    commandStore = new PostgresCommandStore(dataSource, serializer)
    uidGenerator = new PostgresUidGenerator(dataSource)
  }

  after {
    dataSource.close()
  }

}
