package pl.mpieciukiewicz.scalacqrs

trait CommandHandler[C <: Command[_], R] {
  def handle(commandId: CommandId, command: C): R
  def commandType: Class[C]
}
