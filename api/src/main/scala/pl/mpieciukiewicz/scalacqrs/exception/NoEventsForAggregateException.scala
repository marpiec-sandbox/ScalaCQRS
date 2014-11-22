package pl.mpieciukiewicz.scalacqrs.exception

class NoEventsForAggregateException(message: String) extends RuntimeException(message)
