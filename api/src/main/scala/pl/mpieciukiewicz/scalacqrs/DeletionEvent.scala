package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.Event

abstract class DeletionEvent[T](aggregateType:Class[T]) extends Event[T](aggregateType)