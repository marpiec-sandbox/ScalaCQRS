package pl.mpieciukiewicz.postgresimpl

import java.time.Instant

import pl.mpieciukiewicz.postgresimpl.internal.Event

/**
 * ...
 * @author Marcin Pieciukiewicz
 */

case class EventRow[T](commandId: CommandId, aggregateId: AggregateId, version:Int, creationTimestamp: Instant, event: Event[T])
