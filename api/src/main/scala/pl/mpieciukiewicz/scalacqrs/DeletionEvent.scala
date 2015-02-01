package pl.mpieciukiewicz.scalacqrs

abstract class DeletionEvent[T](aggregateType:Class[T]) extends Event[T](aggregateType)