package io.testdomain.memoryimpl

import java.time.Clock

import io.scalacqrs.memoryimpl.{MemoryCommandStore, MemoryEventStore, MemorySequentialUIDGenerator}
import io.scalacqrs.{CommandStore, EventStore, UIDGenerator}
import io.testdomain.BasicUsageScenarioSpec

class MemoryUsageScenarioSpec extends BasicUsageScenarioSpec {

  var eventStore: EventStore = new MemoryEventStore(Clock.systemDefaultZone())
  var commandStore: CommandStore = new MemoryCommandStore(Clock.systemDefaultZone())
  var uidGenerator: UIDGenerator = new MemorySequentialUIDGenerator
}