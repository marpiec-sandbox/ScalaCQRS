package pl.mpieciukiewicz.scalacqrs

trait EventListener[T] {

  def onEvent(aggregateId: AggregateId, event: Event[T])

}
