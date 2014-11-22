package pl.mpieciukiewicz.scalacqrs


abstract class Aggregate(val uid: UID, aggregateVersion: Int) {

  private var _version:Int = aggregateVersion

  def incrementVersion() {
    _version += 1
  }

  def version = _version

  def copy: Aggregate


}
