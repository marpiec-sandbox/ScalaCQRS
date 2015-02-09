package pl.mpieciukiewicz.scalacqrs.commandhandler

import java.lang.reflect.Type

import pl.mpieciukiewicz.scalacqrs.CommandId
import pl.mpieciukiewicz.scalacqrs.command.Command
import pl.mpieciukiewicz.scalacqrs.data.UserId
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

abstract class CommandHandler[C <: Command[R], R] {
  def handle(commandId: CommandId, userId: UserId, command: C): R
  def commandClass: Class[C] = {
    var clazz = this.getClass.asInstanceOf[Class[_]]
    while(clazz.getGenericSuperclass.isInstanceOf[Class[_]]) {
      clazz = clazz.getGenericSuperclass.asInstanceOf[Class[_]]
    }
    val arguments: Array[Type] = clazz.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[C]]
  }
}
