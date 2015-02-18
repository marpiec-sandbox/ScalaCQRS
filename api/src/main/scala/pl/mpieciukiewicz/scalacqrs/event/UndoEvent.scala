package pl.mpieciukiewicz.scalacqrs.event

class UndoEvent[A](val eventsCount: Int) extends Event[A]
