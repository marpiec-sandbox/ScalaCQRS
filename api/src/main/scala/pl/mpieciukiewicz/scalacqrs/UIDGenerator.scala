package pl.mpieciukiewicz.scalacqrs

trait UIDGenerator {
  def nextUID: UID
}
