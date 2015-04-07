package io.scalacqrs.event

abstract class UndoEvent[A](val eventsCount: Int) extends Event[A]
