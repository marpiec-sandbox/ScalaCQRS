package pl.mpieciukiewicz.scalacqrs.memoryimpl

import org.slf4j.LoggerFactory
import pl.mpieciukiewicz.scalacqrs.{UID, Aggregate, DataStore, EventStore}

class MemoryDataStore(val eventStore: EventStore) extends DataStore {

  private val Log = LoggerFactory.getLogger(classOf[MemoryDataStore])

  override def getAggregateByVersion[T <: Aggregate](aggregateClass: Class[T], uid: UID, version: Int): T = getAggregateWithOptionalVersion(aggregateClass, uid, Some(version))

  override def getAggregate[T <: Aggregate](aggregateClass: Class[T], uid: UID): T = getAggregateWithOptionalVersion(aggregateClass, uid, None)


  private def getAggregateWithOptionalVersion[T <: Aggregate](aggregateClass: Class[T], uid: UID, version: Option[Int]): T = {
    val eventRows = if(version.isDefined) {
      eventStore.getEventsForAggregateFromVersion(aggregateClass, uid, version.get)
    } else {
      eventStore.getEventsForAggregate(aggregateClass, uid)
    }

    val aggregate = aggregateClass.getConstructor(classOf[UID], classOf[Long]).newInstance(UID.ZERO, 0)

    eventRows.foreach((eventRow) => {
      if (eventRow.expectedAggregateVersion == aggregate.version) {
        eventRow.event.applyEvent(aggregate)
        aggregate.incrementVersion()
      } else {
        throw new IllegalStateException("Unexpected version for aggregate when applying eventRow. " +
          "[aggregateType:" + aggregateClass.getName + ", aggregateId:" + uid + ", aggregateVersion:" +
          aggregate.version + "eventType:" + eventRow.getClass.getName + ", expectedVersion:" + eventRow.expectedAggregateVersion + "]")
      }
    })

    if (Log.isDebugEnabled) {
      Log.debug(eventRows.size + " eventRows applied for aggregate [type:" + aggregateClass.getName + ", uid:" + uid + "]")
    }
    aggregate
  }


}
