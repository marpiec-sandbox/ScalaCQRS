package pl.mpieciukiewicz.scalacqrs.internal

trait Event[T] {

  def entityClass: Class[T]
}
