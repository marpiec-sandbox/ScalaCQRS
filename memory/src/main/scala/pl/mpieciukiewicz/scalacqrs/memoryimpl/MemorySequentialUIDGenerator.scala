package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.util.concurrent.atomic.AtomicLong

import pl.mpieciukiewicz.scalacqrs.{UID, UIDGenerator}

class MemorySequentialUIDGenerator extends UIDGenerator {

  private val uid = new AtomicLong(0L)

  def nextUID = UID(uid.getAndIncrement)

}
