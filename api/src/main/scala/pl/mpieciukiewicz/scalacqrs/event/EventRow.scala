package pl.mpieciukiewicz.scalacqrs.event

import java.time.Instant

import pl.mpieciukiewicz.scalacqrs.CommandId
import pl.mpieciukiewicz.scalacqrs.data.AggregateId


/**
 * ...
 * @author Marcin Pieciukiewicz
 */

case class EventRow[T](commandId: CommandId, aggregateId: AggregateId, version:Int, creationTimestamp: Instant, event: Event[T])
