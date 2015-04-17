package io.scalacqrs

import java.lang.reflect.Type

import event.Event
import eventhandler.EventHandler
import io.scalacqrs.data.AggregateId
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

import collection.mutable
import scala.util.Try

import scala.reflect.runtime.universe._

abstract class DataStore[A](private val eventStore: EventStore) {

  eventStore.registerDataStore(this)

  protected val eventHandlers = mutable.HashMap[Class[Event[A]], EventHandler[A, _ <: Event[A]]]()

  def countAllAggregates(): Long

  def getAllAggregateIds(): Seq[AggregateId]

  def getAggregate(id: AggregateId)(implicit tag: TypeTag[A]): Try[Aggregate[A]]

  def getAggregates(ids: Seq[AggregateId])(implicit tag: TypeTag[A]): Seq[Aggregate[A]]

  def getAggregateByVersion(id: AggregateId, version: Int)(implicit tag: TypeTag[A]): Try[Aggregate[A]]

  /* Method created for optimization. This method should use caches.
  * Caches won't be used on undo events.
  * For now only Event store has good access to events */
  private[scalacqrs] def getAggregateByVersionAndApplyEventToIt(
                            id: AggregateId, version: Int, event: Event[A])(implicit tag: TypeTag[A]): Try[Aggregate[A]]

  /** TypeTag could be better solution */
  def typeInfo: Class[A] = {

    var clazz = this.getClass.asInstanceOf[Class[_]]
    while(clazz.getGenericSuperclass.isInstanceOf[Class[_]]) {
      clazz = clazz.getGenericSuperclass.asInstanceOf[Class[_]]
    }
    val arguments: Array[Type] = clazz.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[A]]
  }
}
