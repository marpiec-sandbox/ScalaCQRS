package pl.mpieciukiewicz.scalacqrs.exception

class AggregateAlreadyExistsException(message: String) extends RuntimeException(message)
