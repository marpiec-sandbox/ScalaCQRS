package io.testdomain.postgresimpl

import io.scalacqrs.postgresimpl.PostgresUidGenerator
import io.testdomain.postgresimpl.jdbc.ConnectionPoolFactory
import org.apache.commons.dbcp2.BasicDataSource
import org.scalatest.{MustMatchers, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import MustMatchers._
import io.scalacqrs.data.AggregateId

import scala.collection.parallel.ForkJoinTaskSupport

class MemorySequentialUIDGeneratorSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {


  var eventStoreDataSource:BasicDataSource = null

  before {
    eventStoreDataSource = ConnectionPoolFactory.createSampleConnectionPool
  }

  after {
    eventStoreDataSource.close()
  }

  feature("Generation of sequential unique identifiers") {

    scenario("Getting few numbers from generator") {

      Given("MemorySequentialGenerator instance")

      val generator = new PostgresUidGenerator(eventStoreDataSource)
      val generations = 100000

      When("Menu UIDs are generated")

      val parRange = Range(0, generations).par
      parRange.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(10))
      val generatedUIDs = parRange.map(el => generator.nextAggregateId).toSet

      Then("Number of unique generated UID is equal to number of generations")

      generatedUIDs must have size generations

    }


  }

  def runInParallel(count: Int)(block: () => Set[AggregateId]): Unit = {


  }
}
