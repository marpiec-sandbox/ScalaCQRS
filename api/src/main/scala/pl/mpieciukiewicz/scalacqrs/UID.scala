package pl.mpieciukiewicz.scalacqrs

object UID {

  val ZERO: UID = new UID(0L)

  def parseOrZero(str: String): UID = {
    try {
      new UID(str.toLong)
    } catch {
      case e: NumberFormatException => ZERO
    }
  }

}

case class UID(uid: Long) {
  override def toString = uid.toString
}
