package pl.mpieciukiewicz.postgresimpl.internal

trait Event[T] {

  def entityClass: Class[T]
}
