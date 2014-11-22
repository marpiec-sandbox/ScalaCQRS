package pl.mpieciukiewicz.scalacqrs

import java.time.Instant

import pl.mpieciukiewicz.scalacqrs.internal.AbstractEvent

/**
 * ...
 * @author Marcin Pieciukiewicz
 */

case class EventRow[T](userId: UID, aggregateId: UID, expectedAggregateVersion:Int, creationTimestamp: Instant, event: AbstractEvent[T])
