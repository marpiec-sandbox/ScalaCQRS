package pl.mpieciukiewicz.scalacqrs.jdbcimpl

trait EventSerializer {

  def toJson(obj: AnyRef): String

  def fromJson[E](json: String, clazz: Class[E]): E

}
