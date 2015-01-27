package pl.mpieciukiewicz.postgresimpl

object AggregateId {

  val ZERO: AggregateId = new AggregateId(0L)

  def parseOrZero(str: String): AggregateId = {
    try {
      new AggregateId(str.toLong)
    } catch {
      case e: NumberFormatException => ZERO
    }
  }

}

case class AggregateId(uid: Long) {
  override def toString = uid.toString
}
