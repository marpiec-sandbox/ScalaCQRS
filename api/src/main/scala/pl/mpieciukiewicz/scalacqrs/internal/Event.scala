package pl.mpieciukiewicz.scalacqrs.internal

trait Event[T] {

  def aggregateType(implicit m: Manifest[T]):Class[T] = m.runtimeClass.asInstanceOf[Class[T]]
}
