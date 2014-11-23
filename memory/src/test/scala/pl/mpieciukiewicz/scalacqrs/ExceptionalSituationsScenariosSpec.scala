package pl.mpieciukiewicz.scalacqrs

import java.time.Clock

import org.fest.assertions.api.Assertions.assertThat

import org.scalatest.{FeatureSpec, GivenWhenThen}
import pl.mpieciukiewicz.domain.user.UserCommand
import pl.mpieciukiewicz.domain.user.entity.{Address, User}
import pl.mpieciukiewicz.scalacqrs.memoryimpl.{MemorySequentialUIDGenerator, MemoryDataStore, MemoryEventStore}

class ExceptionalSituationsScenariosSpec extends FeatureSpec with GivenWhenThen {

  feature("???") {

    scenario("???") {

      Given("???")

      When("???")

      Then("???")
    }
  }

  feature("???") {

    scenario("???") {

      Given("???")

      When("???")

      Then("???")
    }
  }
}
