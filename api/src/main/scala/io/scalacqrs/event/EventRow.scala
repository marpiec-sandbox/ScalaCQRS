package io.scalacqrs.event

import java.time.Instant

import io.scalacqrs.CommandId
import io.scalacqrs.data.{UserId, AggregateId}


case class EventRow[T](commandId: CommandId,
                       userId: UserId,
                       aggregateId: AggregateId,
                       version:Int,
                       creationTimestamp: Instant,
                       event: Event[T])
