package pl.mpieciukiewicz.scalacqrs

trait CommandHandler[C, R] {
  def handle(commandId: CommandId, command: C): R
  def commandType(implicit m: Manifest[C]):Class[C] = m.runtimeClass.asInstanceOf[Class[C]]
}
