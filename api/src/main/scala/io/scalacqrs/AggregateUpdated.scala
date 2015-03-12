package io.scalacqrs

import io.scalacqrs.data.AggregateId
import io.scalacqrs.event.Event

case class AggregateUpdated[T](aggregateId: AggregateId, event: Event[T])
