package pl.mpieciukiewicz.scalacqrs.eventhandler

import java.lang.reflect.Type

import pl.mpieciukiewicz.scalacqrs.event.Event
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

abstract class EventHandler[A, E <: Event[A]] {
  def aggregateClass: Class[A] = {
    var clazz = this.getClass.asInstanceOf[Class[_]]
    while(clazz.getGenericSuperclass.isInstanceOf[Class[_]]) {
      clazz = clazz.getGenericSuperclass.asInstanceOf[Class[_]]
    }
    val arguments: Array[Type] = clazz.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[A]]
  }
  def eventClass: Class[E] = {
    var clazz = this.getClass.asInstanceOf[Class[_]]
    while(clazz.getGenericSuperclass.isInstanceOf[Class[_]]) {
      clazz = clazz.getGenericSuperclass.asInstanceOf[Class[_]]
    }
    val arguments: Array[Type] = clazz.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(1).asInstanceOf[Class[E]]
  }
}
