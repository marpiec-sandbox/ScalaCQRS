package pl.mpieciukiewicz.scalacqrs

object CommandId {
  val ZERO: CommandId = new CommandId(0L)

  def parseOrZero(str: String): CommandId = {
    try {
      new CommandId(str.toLong)
    } catch {
      case e: NumberFormatException => ZERO
    }
  }
}


case class CommandId(uid: Long) {
  override def toString = uid.toString
}
