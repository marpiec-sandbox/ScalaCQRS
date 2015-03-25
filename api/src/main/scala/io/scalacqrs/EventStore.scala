package io.scalacqrs

import io.scalacqrs.data.{UserId, AggregateId}
import io.scalacqrs.event.{EventRow, Event}

import scala.collection.mutable


// depends on datastore constructor
trait EventStore {

  private var dataStores: Map[Class[_], DataStore[_]] = Map()

  private val eventListeners = mutable.Map[Class[_], mutable.ListBuffer[AggregateUpdated[_] => Unit]]()
  private val stateChangedListeners = mutable.Map[Class[_], mutable.ListBuffer[AggregateState[_] => Unit]]()

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId]

  def addFirstEvent(commandId: CommandId, userId: UserId, newAggregateId: AggregateId, event: Event[_])

  def addEvent(commandId: CommandId, userId: UserId, aggregateId: AggregateId, expectedVersion: Int, event: Event[_])

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): Seq[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): Seq[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): Seq[EventRow[T]]

  def registerDataStore[A](store: DataStore[A]): Unit = {
    dataStores += store.typeInfo -> store
  }

  def addEventListener[T](aggregateClass: Class[T], eventListener: AggregateUpdated[T] => Unit): Unit = {

    val eventListenersForType = eventListeners
      .getOrElseUpdate(aggregateClass, mutable.ListBuffer())
    eventListenersForType += eventListener.asInstanceOf[AggregateUpdated[_] => Unit]
  }

  def addStateChangedListener[T](aggregateClass: Class[T], eventListener: AggregateState[T] => Unit): Unit = {

    val eventListenersForType = stateChangedListeners
      .getOrElseUpdate(aggregateClass, mutable.ListBuffer())
    eventListenersForType += eventListener.asInstanceOf[AggregateState[_] => Unit]
  }

  protected def callUpdateListeners[T](aggregateId: AggregateId, version: Int, event: Event[T]): Unit = {
    def callEventListners(): Unit = {
      val eventUpdate = AggregateUpdated(aggregateId, version, event)

      eventListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
        .foreach(_.apply(eventUpdate))
    }

  /** assumed that call from inside of framework should always return success */
    def aggragateState: Aggregate[T] = {
      val store = dataStores(event.aggregateType).asInstanceOf[DataStore[T]]
      version match {
        case 1 => store.getAggregate(aggregateId).get
        case _ => store.getAggregateByVersionAndApplyEventToIt(aggregateId, version -1, event).get
      }
    }

    def callStateListners(): Unit = {
      val stateUpdate = AggregateState(aggragateState, event)
      /** sending whole state is bound to same triggers as eventListners */
      stateChangedListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
        .foreach(_.apply(stateUpdate))
    }

    callEventListners()
    callStateListners()
  }
}
