package io.testdomain.postgresimpl

import io.scalacqrs.postgresimpl.ObjectSerializer
import pl.mpieciukiewicz.mpjsons.MPJsons

import scala.reflect.runtime.universe._

class JsonSerializer extends ObjectSerializer {

  val mpjsons = new MPJsons

  override def toJson[E](obj: AnyRef)
                        (implicit tag: TypeTag[E]): String = mpjsons.serialize(obj)

  override def fromJson[E](json: String, tpe: Type): E = mpjsons.deserialize(json, tpe)
}
