package pl.mpieciukiewicz.scalacqrs.event

import java.lang.reflect.Type

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

abstract class Event[A] {
  def aggregateType:Class[A] = {

    var clazz = this.getClass.asInstanceOf[Class[_]]
    while(clazz.getGenericSuperclass.isInstanceOf[Class[_]]) {
      clazz = clazz.getGenericSuperclass.asInstanceOf[Class[_]]
    }
    val arguments: Array[Type] = clazz.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[A]]
  }
}
