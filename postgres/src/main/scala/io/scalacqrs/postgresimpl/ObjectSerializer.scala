package io.scalacqrs.postgresimpl

import scala.reflect.runtime.universe._

trait ObjectSerializer {

  def toJson[E <: AnyRef : TypeTag](obj: E): String

  def fromJson[E](json: String, typeName: String): E

}
