package pl.mpieciukiewicz.scalacqrs

trait EventListener[T] {

  def onEvent(aggregateUpdated: AggregateUpdated[T])

}
