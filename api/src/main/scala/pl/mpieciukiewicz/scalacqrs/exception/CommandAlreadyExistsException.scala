package pl.mpieciukiewicz.scalacqrs.exception

class CommandAlreadyExistsException(message: String) extends RuntimeException(message)