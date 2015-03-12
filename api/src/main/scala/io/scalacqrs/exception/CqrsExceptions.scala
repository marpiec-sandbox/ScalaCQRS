package io.scalacqrs.exception

sealed abstract class CqrsException(message: String) extends Exception(message) {
  // This prohibits exception to gather stack trace info thous this exception is much faster
  override def fillInStackTrace() = this
}

class AggregateAlreadyExistsException(message: String) extends CqrsException(message)
class AggregateWasAlreadyDeletedException(message: String) extends CqrsException(message)
class CommandAlreadyExistsException(message: String) extends CqrsException(message)
class ConcurrentAggregateModificationException(message: String) extends CqrsException(message)
class NoEventsForAggregateException(message: String) extends CqrsException(message)
class IncorrectAggregateVersionException(message: String) extends CqrsException(message)