package io.testdomain.memoryimpl

import io.scalacqrs.memoryimpl.MemorySequentialUIDGenerator
import org.fest.assertions.api.Assertions.assertThat

import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParRange
import org.scalatest.{FeatureSpec, GivenWhenThen}

class MemorySequentialUIDGeneratorSpec extends FeatureSpec with GivenWhenThen {


  feature("Generation of sequential unique identifiers") {

    scenario("Getting few numbers from generator") {

      Given("MemorySequentialGenerator instance")

      val generator = new MemorySequentialUIDGenerator()
      val generations = 100000


      When("Menu UIDs are generated")

      val parRange: ParRange = Range(0, generations).par
      parRange.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(10))
      val generatedUIDs = parRange.map(el => generator.nextAggregateId).toSeq


      Then("Number of unique generated UID is equal to number of generations")

      assertThat(generatedUIDs.size).isEqualTo(generations)

    }


  }


}
