package io.testdomain.postgresimpl

import io.mpjsons.MPJsons
import io.scalacqrs.postgresimpl.ObjectSerializer

import scala.reflect.runtime.universe._

class JsonSerializer extends ObjectSerializer {

  val mpjsons = new MPJsons

  override def toJson[E](obj: AnyRef)
                        (implicit tag: TypeTag[E]): String =
    if (tag == typeTag[Nothing]) {
      mpjsons.serialize(obj, obj.getClass.getName)
    } else {
      mpjsons.serialize(obj)
    }


  override def fromJson[E](json: String, tpe: Type): E = mpjsons.deserialize(json, tpe)
}
