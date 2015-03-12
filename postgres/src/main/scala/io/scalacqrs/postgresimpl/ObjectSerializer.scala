package io.scalacqrs.postgresimpl

trait ObjectSerializer {

  def toJson(obj: AnyRef): String

  def fromJson[E](json: String, clazz: Class[E]): E

}