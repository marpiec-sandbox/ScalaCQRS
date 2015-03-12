package pl.mpieciukiewicz.scalacqrs.event

import java.time.Instant

import pl.mpieciukiewicz.scalacqrs.CommandId
import pl.mpieciukiewicz.scalacqrs.data.{UserId, AggregateId}


/**
 * ...
 * @author Marcin Pieciukiewicz
 */

case class EventRow[T](commandId: CommandId,
                       userId: UserId,
                       aggregateId: AggregateId,
                       version:Int,
                       creationTimestamp: Instant,
                       event: Event[T])
