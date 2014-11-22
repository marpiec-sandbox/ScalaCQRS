package pl.mpieciukiewicz.scalacqrs.memoryimpl

import java.lang
import java.lang.reflect.Constructor

import org.slf4j.LoggerFactory
import pl.mpieciukiewicz.scalacqrs.{UID, Aggregate, DataStore, EventStore}

class MemoryDataStore(val eventStore: EventStore) extends DataStore {

  private val Log = LoggerFactory.getLogger(classOf[MemoryDataStore])
  private val boxedZero: lang.Long = Long.box(0)

  override def getAggregateByVersion[T <: Aggregate](aggregateClass: Class[T], uid: UID, version: Int): T = getAggregateWithOptionalVersion(aggregateClass, uid, Some(version))

  override def getAggregate[T <: Aggregate](aggregateClass: Class[T], uid: UID): T = getAggregateWithOptionalVersion(aggregateClass, uid, None)


  private def getAggregateWithOptionalVersion[T <: Aggregate](aggregateClass: Class[T], uid: UID, version: Option[Int]): T = {
    val eventRows = if(version.isDefined) {
      eventStore.getEventsForAggregateFromVersion(aggregateClass, uid, version.get)
    } else {
      eventStore.getEventsForAggregate(aggregateClass, uid)
    }

    val constructor: Constructor[T] = aggregateClass.getConstructor(Array[Class[_]](classOf[UID], classOf[Long]): _*)

    val aggregate = constructor.newInstance(UID.ZERO, boxedZero)

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
