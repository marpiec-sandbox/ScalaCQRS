package pl.mpieciukiewicz.scalacqrs

import org.fest.assertions.api.Assertions.assertThat

import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.scalacqrs.memoryimpl.MemorySequentialUIDGenerator

import scala.collection.parallel.{ForkJoinTaskSupport}
import scala.collection.parallel.immutable.{ParRange}

class MemorySequentialUIDGeneratorSpec extends FeatureSpec with GivenWhenThen {


  feature("Generation of sequential unique identifiers") {

    scenario("Getting few numbers from generator") {

      Given("MemorySequentialGenerator instance")

      val generator = new MemorySequentialUIDGenerator()
      val generations = 100000


      When("Menu UIDs are generated")

      val parRange: ParRange = Range(0, generations).par
      parRange.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(10))
      val generatedUIDs = parRange.map(el => generator.nextUID).toSet


      Then("Number of unique generated UID is equal to number of generations")

      assertThat(generatedUIDs.size).isEqualTo(generations)

    }


  }

  def runInParallel(count: Int)(block: () => Set[UID]): Unit = {


  }
}
