package io.scalacqrs

import io.scalacqrs.data.AggregateId
import io.scalacqrs.event.Event

/**
 * Message to communicate new event which modified state of aggregate
 * @param event Event which updates actual state od aggregate
 */
case class AggregateUpdated[T](aggregateId: AggregateId, version: Int, event: Event[T])

/**
  * Message to communicate new state due to new event
  * @param lastEvent Last Event which took aggregate to actual state
  */
case class AggregateState[T](aggregate: Aggregate[T], version: Int, lastEvent: Event[T])
