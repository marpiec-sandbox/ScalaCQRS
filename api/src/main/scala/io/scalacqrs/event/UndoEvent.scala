package io.scalacqrs.event

class UndoEvent[A](val eventsCount: Int) extends Event[A]
