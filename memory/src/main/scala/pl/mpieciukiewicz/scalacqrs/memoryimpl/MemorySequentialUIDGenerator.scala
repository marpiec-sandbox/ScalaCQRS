package pl.mpieciukiewicz.scalacqrs.memoryimpl

import pl.mpieciukiewicz.scalacqrs.{UID, UIDGenerator}

class MemorySequentialUIDGenerator extends UIDGenerator {
  private var uid: Long = 0L

  def nextUID = {
    uid += 1
    new UID(uid)
  }
}
