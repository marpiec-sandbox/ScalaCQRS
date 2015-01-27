package pl.mpieciukiewicz.scalacqrs.core

import org.slf4j.LoggerFactory
import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.exception.AggregateWasAlreadyDeletedException

class CoreDataStore(val eventStore: EventStore) extends DataStore {

  private val Log = LoggerFactory.getLogger(classOf[CoreDataStore])

  override def getAggregateByVersion[T](aggregateClass: Class[T], uid: AggregateId, version: Int): Aggregate[T] = getAggregateWithOptionalVersion(aggregateClass, uid, Some(version))

  override def getAggregate[T](aggregateClass: Class[T], uid: AggregateId): Aggregate[T] = getAggregateWithOptionalVersion(aggregateClass, uid, None)


  private def getAggregateWithOptionalVersion[T](aggregateRootClass: Class[T], uid: AggregateId, version: Option[Int]): Aggregate[T] = {
    val eventRows = if (version.isDefined) {
      if (version.get < 1) {
        throw new IllegalArgumentException("Cannot get aggregates for versions lower than 1")
      } else {
        eventStore.getEventsForAggregateToVersion(aggregateRootClass, uid, version.get)
      }
    } else {
      eventStore.getEventsForAggregate(aggregateRootClass, uid)
    }

    if (eventRows.isEmpty) {
      throw new IllegalStateException("Aggregate of type " + aggregateRootClass + " does not exist.")
    }


    val creatorEventRow: EventRow[T] = eventRows.head
    if (creatorEventRow.version != 1) {
      throw new IllegalStateException("CreatorEvent need to be of version 1, as it always first event for an aggregate. ("+
        creatorEventRow.event.getClass+" has version "+creatorEventRow.version+")")
    }
    val aggregateRoot = creatorEventRow.event.asInstanceOf[CreationEvent[T]].applyEvent()

    var aggregate = Aggregate(uid, 1, Some(aggregateRoot))

    eventRows.tail.foreach((eventRow) => {
      if (eventRow.version == aggregate.version + 1 && aggregate.aggregateRoot.isDefined) {
        aggregate = eventRow.event match {
          case event: ModificationEvent[T] => Aggregate(aggregate.uid, aggregate.version + 1, Some(event.applyEvent(aggregateRoot)))
          case event: DeletionEvent[T] => Aggregate(aggregate.uid, aggregate.version + 1, None)
        }
      } else if (aggregate.aggregateRoot.isEmpty) {
        throw new AggregateWasAlreadyDeletedException("Unexpected modification of already deleted aggregate")
      } else {
        throw new IllegalStateException("Unexpected version for aggregate when applying eventRow. " +
          "[aggregateType:" + aggregateRootClass.getName + ", aggregateId:" + uid + ", aggregateVersion:" +
          aggregate.version + ", eventType:" + eventRow.getClass.getName + ", expectedVersion:" + eventRow.version + "]")
      }
    })

    if (Log.isDebugEnabled) {
      Log.debug(eventRows.size + " eventRows applied for aggregate [type:" + aggregateRootClass.getName + ", uid:" + uid + "]")
    }
    aggregate
  }


}
