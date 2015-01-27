package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.util.concurrent.atomic.AtomicLong

import pl.mpieciukiewicz.scalacqrs._

class MemorySequentialUIDGenerator extends UIDGenerator {

  private val uid = new AtomicLong(0L)

  override def nextAggregateId = AggregateId(uid.getAndIncrement)

  override def nextCommandId =  CommandId(uid.getAndIncrement)
  
  override def nextUserId =  UserId(uid.getAndIncrement)
  
}
