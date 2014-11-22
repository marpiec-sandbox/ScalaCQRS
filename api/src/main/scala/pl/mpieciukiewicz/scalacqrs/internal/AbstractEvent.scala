package pl.mpieciukiewicz.scalacqrs.internal

trait AbstractEvent[T] {

  def apply():T

  def apply(entity: T):T

  def entityClass: Class[T]
}
