package pl.mpieciukiewicz.scalacqrs.internal

trait Event[T] {

  def aggregateType:Class[T]
}
