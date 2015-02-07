package pl.mpieciukiewicz.scalacqrs.event

import java.lang.reflect.Type

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

abstract class Event[A] {
  def aggregateType:Class[A] = {
    val arguments: Array[Type] = this.getClass.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[A]]
  }
}
