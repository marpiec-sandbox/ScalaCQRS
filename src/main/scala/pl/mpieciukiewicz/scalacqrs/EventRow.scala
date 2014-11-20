package pl.mpieciukiewicz.scalacqrs

import java.time.Instant

/**
 * ...
 * @author Marcin Pieciukiewicz
 */

case class EventRow(userId: UID, aggregateId: UID, expectedAggregateVersion:Int, creationTimestamp: Instant, event: Event[_])
