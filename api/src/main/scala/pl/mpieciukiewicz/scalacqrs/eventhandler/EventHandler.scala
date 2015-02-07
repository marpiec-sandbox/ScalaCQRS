package pl.mpieciukiewicz.scalacqrs.eventhandler

import java.lang.reflect.Type

import pl.mpieciukiewicz.scalacqrs.event.Event
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

abstract class EventHandler[A, E <: Event[A]] {
  def aggregateClass: Class[A] = {
    val arguments: Array[Type] = this.getClass.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[A]]
  }
  def eventClass: Class[E] = {
    val arguments: Array[Type] = this.getClass.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(1).asInstanceOf[Class[E]]
  }
}
