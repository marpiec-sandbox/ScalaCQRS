package io.scalacqrs.postgresimpl

import scala.reflect.runtime.universe._

trait ObjectSerializer {

  def toJson[E](obj: AnyRef)
               (implicit tag: TypeTag[E]): String

  def fromJson[E](json: String, tpe: Type): E

}
