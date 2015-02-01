package pl.mpieciukiewicz.scalacqrs

abstract class Event[T](val aggregateType:Class[T])
