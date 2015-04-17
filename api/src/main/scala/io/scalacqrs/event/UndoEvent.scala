package io.scalacqrs.event

abstract class UndoEvent[A] extends Event[A] {
  val eventsCount: Int
}
