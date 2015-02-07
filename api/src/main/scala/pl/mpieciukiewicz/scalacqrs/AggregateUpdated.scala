package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.data.AggregateId
import pl.mpieciukiewicz.scalacqrs.event.Event

case class AggregateUpdated[T](aggregateId: AggregateId, event: Event[T])
