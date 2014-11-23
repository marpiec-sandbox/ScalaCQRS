package pl.mpieciukiewicz.scalacqrs.exception

class AggregateWasAlreadyDeletedException(message: String) extends RuntimeException(message)
