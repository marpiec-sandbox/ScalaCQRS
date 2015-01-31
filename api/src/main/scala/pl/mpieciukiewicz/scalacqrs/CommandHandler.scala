package pl.mpieciukiewicz.scalacqrs

abstract class CommandHandler[C <: Command[_], R](val commandType: Class[C]) {
  def handle(commandId: CommandId, command: C): R
}
