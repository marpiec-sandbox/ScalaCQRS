package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.Event
import pl.mpieciukiewicz.scalacqrs.eventhandler.EventHandler

trait DataStore {

  def registerHandler[A, E <: Event[A]](eventHandler: EventHandler[A, E])

  def countAllAggregates[T](aggregateClass: Class[T]): Long

  def getAllAggregateIds[T](aggregateClass: Class[T]):Seq[AggregateId]

  def getAggregate[T](aggregateClass: Class[T], id: AggregateId): Aggregate[T]

  def getAggregates[T](aggregateClass: Class[T], ids: Seq[AggregateId]): Map[AggregateId, Aggregate[T]]

  def getAggregateByVersion[T](aggregateClass: Class[T], uid: AggregateId, version: Int): Aggregate[T]
}
