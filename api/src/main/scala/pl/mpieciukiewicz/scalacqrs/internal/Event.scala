package pl.mpieciukiewicz.scalacqrs.internal

abstract class Event[T](val aggregateType:Class[T])
