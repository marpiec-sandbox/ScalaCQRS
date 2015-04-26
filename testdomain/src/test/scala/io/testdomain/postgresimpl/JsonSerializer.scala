package io.testdomain.postgresimpl

import io.mpjsons.MPJsons
import io.scalacqrs.postgresimpl.ObjectSerializer

import scala.reflect.runtime.universe._

class JsonSerializer extends ObjectSerializer {

  val mpjsons = new MPJsons

  override def toJson[E : TypeTag](obj: E): String = {
    mpjsons.serialize[E](obj)
  }


  override def fromJson[E](json: String, typeName: String): E = mpjsons.deserialize(json, typeName)
}
