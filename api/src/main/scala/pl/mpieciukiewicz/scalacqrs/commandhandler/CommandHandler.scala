package pl.mpieciukiewicz.scalacqrs.commandhandler

import java.lang.reflect.Type

import pl.mpieciukiewicz.scalacqrs.CommandId
import pl.mpieciukiewicz.scalacqrs.command.Command
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

abstract class CommandHandler[C <: Command[R], R] {
  def handle(commandId: CommandId, command: C): R
  def commandClass: Class[C] = {
    val arguments: Array[Type] = this.getClass.getGenericSuperclass.asInstanceOf[ParameterizedTypeImpl].getActualTypeArguments
    arguments(0).asInstanceOf[Class[C]]
  }
}
