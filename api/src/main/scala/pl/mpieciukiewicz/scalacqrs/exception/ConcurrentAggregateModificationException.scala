package pl.mpieciukiewicz.scalacqrs.exception

class ConcurrentAggregateModificationException(message: String) extends RuntimeException(message)