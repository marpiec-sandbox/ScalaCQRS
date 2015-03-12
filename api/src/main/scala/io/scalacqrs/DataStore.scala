package io.scalacqrs

import io.scalacqrs.data.AggregateId

import scala.util.Try

trait DataStore[A] {

  def countAllAggregates(): Long

  def getAllAggregateIds():Seq[AggregateId]

  def getAggregate(id: AggregateId): Try[Aggregate[A]]

  def getAggregates(ids: Seq[AggregateId]): Seq[Aggregate[A]]

  def getAggregateByVersion(id: AggregateId, version: Int): Try[Aggregate[A]]
}
