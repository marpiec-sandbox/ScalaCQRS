package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.Event
import pl.mpieciukiewicz.scalacqrs.eventhandler.EventHandler

import scala.util.Try

trait DataStore[A] {

  def countAllAggregates(): Long

  def getAllAggregateIds():Seq[AggregateId]

  def getAggregate(id: AggregateId): Try[Aggregate[A]]

  def getAggregates(ids: Seq[AggregateId]): Seq[Aggregate[A]]

  def getAggregateByVersion(id: AggregateId, version: Int): Try[Aggregate[A]]
}
