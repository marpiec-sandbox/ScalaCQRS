package io.scalacqrs

import io.scalacqrs.data.{UserId, AggregateId}
import io.scalacqrs.event.{EventRow, Event}

import scala.collection.mutable
import scala.reflect.runtime.universe._


// depends on datastore constructor
trait EventStore {

  private var dataStores: Map[Type, DataStore[AnyRef]] = Map()

  private val eventListeners = mutable.Map[Type, mutable.ListBuffer[AggregateUpdated[Event[AnyRef]] => Unit]]()
  private val stateChangedListeners = mutable.Map[Type, mutable.ListBuffer[AggregateState[Event[AnyRef]] => Unit]]()

  def countAllAggregates[A: TypeTag]: Long

  def getAllAggregateIds[A: TypeTag]: Seq[AggregateId]

  def addFirstEvent[E <: Event[_]: TypeTag](commandId: CommandId, userId: UserId, newAggregateId: AggregateId, event: E)

  def addEvent[E <: Event[_]: TypeTag](commandId: CommandId, userId: UserId, aggregateId: AggregateId, expectedVersion: Int, event: E)

  def getEventsForAggregate[A: TypeTag](uid: AggregateId): Seq[EventRow[Event[A]]]

  def getEventsForAggregateFromVersion[A: TypeTag](uid: AggregateId, fromVersion: Int): Seq[EventRow[Event[A]]]

  def getEventsForAggregateToVersion[A: TypeTag](uid: AggregateId, toVersion: Int): Seq[EventRow[Event[A]]]

  def registerDataStore[A](store: DataStore[A]): Unit = {
    dataStores += store.typeInfo -> store.asInstanceOf[DataStore[AnyRef]]
  }

  def addEventListener[A: TypeTag](eventListener: AggregateUpdated[Event[A]] => Unit): Unit = {

    val eventListenersForType = eventListeners
      .getOrElseUpdate(typeOf[A], mutable.ListBuffer())
    eventListenersForType += eventListener.asInstanceOf[AggregateUpdated[Event[AnyRef]] => Unit]
  }

  def addStateChangedListener[A: TypeTag](eventListener: AggregateState[Event[A]] => Unit): Unit = {

    val eventListenersForType = stateChangedListeners
      .getOrElseUpdate(typeOf[A], mutable.ListBuffer())
    eventListenersForType += eventListener.asInstanceOf[AggregateState[Event[AnyRef]] => Unit]
  }

  protected def callUpdateListeners[E <: Event[_] : TypeTag](aggregateId: AggregateId, version: Int, event: E): Unit = {
    def callEventListeners(): Unit = {
      val eventUpdate = AggregateUpdated(aggregateId, version, event)

      eventListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
        .foreach(_.apply(eventUpdate.asInstanceOf[AggregateUpdated[Event[AnyRef]]]))
    }

  /** assumed that call from inside of framework should always return success */
    def aggragateState: Aggregate[_] = {


    val tpe = typeOf[E]
    val store = dataStores(tpe.baseType(tpe.baseClasses.filter(_.fullName == classOf[Event[AnyRef]].getName).head).typeArgs.head)
      version match {
        case 1 => store.getAggregate(aggregateId).get
        case _ => store.getAggregateByVersionAndApplyEventToIt(aggregateId, version -1, event.asInstanceOf[Event[AnyRef]]).get
      }
    }

    def callStateListners(): Unit = {
      val stateUpdate = AggregateState(aggragateState, event)
      /** sending whole state is bound to same triggers as eventListners */
      stateChangedListeners.getOrElse(event.aggregateType, mutable.ListBuffer())
        .foreach(_.apply(stateUpdate.asInstanceOf[AggregateState[Event[AnyRef]]]))
    }

    callEventListeners()
    callStateListners()
  }
}
