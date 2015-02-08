package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.Event
import pl.mpieciukiewicz.scalacqrs.eventhandler.EventHandler

trait DataStore[A] {

  def countAllAggregates(): Long

  def getAllAggregateIds():Seq[AggregateId]

  def getAggregate(id: AggregateId): Aggregate[A]

  def getAggregates(ids: Seq[AggregateId]): Map[AggregateId, Aggregate[A]]

  def getAggregateByVersion(id: AggregateId, version: Int): Aggregate[A]
}
