package pl.mpieciukiewicz.scalacqrs


case class Aggregate[T](uid: UID, version: Int, aggregateRoot: T) {

  def incrementVersion() {
    copy(version = version + 1)
  }

}
