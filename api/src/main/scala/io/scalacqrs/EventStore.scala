package io.scalacqrs

import io.scalacqrs.data.{UserId, AggregateId}
import io.scalacqrs.event.{EventRow, Event}

import scala.collection.mutable
import scala.reflect.runtime.universe._


// depends on datastore constructor
trait EventStore {

  private var dataStores: Map[Class[_], DataStore[_]] = Map()

  private val eventListeners = mutable.Map[Class[_], mutable.ListBuffer[AggregateUpdated[_] => Unit]]()
  private val stateChangedListeners = mutable.Map[Class[_], mutable.ListBuffer[AggregateState[_] => Unit]]()

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]): Seq[AggregateId]

  def addFirstEvent[A: TypeTag, E <: Event[A]: TypeTag](commandId: CommandId, userId: UserId, newAggregateId: AggregateId, event: E)

  def addEvent[A: TypeTag, E <: Event[A]: TypeTag](commandId: CommandId, userId: UserId, aggregateId: AggregateId, expectedVersion: Int, event: E)

  def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId)
                              (implicit tag: TypeTag[T]): Seq[EventRow[T]]

  def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int)
                                         (implicit tag: TypeTag[T]): Seq[EventRow[T]]

  def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int)
                                       (implicit tag: TypeTag[T]): Seq[EventRow[T]]

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

  protected def callUpdateListeners[A : TypeTag](aggregateId: AggregateId, version: Int, event: Event[A]): Unit = {
    def callEventListners(): Unit = {
      val eventUpdate = AggregateUpdated(aggregateId, version, event)

      eventListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
        .foreach(_.apply(eventUpdate))
    }

  /** assumed that call from inside of framework should always return success */
    def aggragateState: Aggregate[A] = {
      val store = dataStores(event.aggregateType).asInstanceOf[DataStore[A]]
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
