package io.scalacqrs

import io.scalacqrs.data.AggregateId
import io.scalacqrs.event.Event

/**
 * Message to communicate new event which modified state of aggregate
 * @param event Event which updates actual state od aggregate
 */
case class AggregateUpdated[+E <: Event[_]](aggregateId: AggregateId, version: Int, event: E)

/**
  * Message to communicate new state due to new event
  * @param lastEvent Last Event which took aggregate to actual state
  */
case class AggregateState[+E <: Event[_]](aggregate: Aggregate[_], lastEvent: E)
