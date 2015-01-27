package pl.mpieciukiewicz.memoryimpl

import java.util.concurrent.atomic.AtomicLong

import pl.mpieciukiewicz.postgresimpl._

class MemorySequentialUIDGenerator extends UIDGenerator {

  private val uid = new AtomicLong(0L)

  override def nextAggregateId = AggregateId(uid.getAndIncrement)

  override def nextCommandId =  CommandId(uid.getAndIncrement)
  
  override def nextUserId =  UserId(uid.getAndIncrement)
  
}
