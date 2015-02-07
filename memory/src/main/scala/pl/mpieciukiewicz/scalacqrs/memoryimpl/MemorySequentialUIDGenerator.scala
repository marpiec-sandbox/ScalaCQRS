package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.util.concurrent.atomic.AtomicLong

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.data.AggregateId

class MemorySequentialUIDGenerator extends UIDGenerator {

  private val aggregateUid = new AtomicLong(0L)
  private val commandUid = new AtomicLong(0L)

  override def nextAggregateId = AggregateId(aggregateUid.getAndIncrement)

  override def nextCommandId =  CommandId(commandUid.getAndIncrement)
  
}
