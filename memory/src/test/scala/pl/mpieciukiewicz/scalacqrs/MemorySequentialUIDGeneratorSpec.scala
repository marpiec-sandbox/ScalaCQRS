package pl.mpieciukiewicz.scalacqrs

import org.fest.assertions.api.Assertions.assertThat

import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.scalacqrs.memoryimpl.MemorySequentialUIDGenerator

import scala.collection.mutable

class MemorySequentialUIDGeneratorSpec extends FeatureSpec with GivenWhenThen {


  feature("Generation of sequential unique identifiers") {

    scenario("Getting few numbers from generator") {

      Given("MemorySequentialGenerator instance")

      val generator = new MemorySequentialUIDGenerator()
      val generations = 100000


      When("Menu UIDs are generated")
      val generatedUids = mutable.HashSet[UID]()

      for (i <- 0 until generations) {
        generatedUids += generator.nextUID
      }

      Then("Number of unique generated UID is equal to number of generations")

      assertThat(generatedUids.size).isEqualTo(generations)

    }


  }
}
