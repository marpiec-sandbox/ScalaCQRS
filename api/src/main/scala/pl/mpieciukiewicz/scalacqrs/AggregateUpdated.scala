package pl.mpieciukiewicz.scalacqrs

case class AggregateUpdated[T](aggregateId: AggregateId, event: Event[T])
