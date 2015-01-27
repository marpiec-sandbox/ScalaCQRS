package pl.mpieciukiewicz.postgresimpl

import pl.mpieciukiewicz.mpjsons.MPJson
import pl.mpieciukiewicz.postgresimpl.postgresimpl.ObjectSerializer

class JsonSerializer extends ObjectSerializer {

  override def toJson(obj: AnyRef): String = MPJson.serialize(obj)

  override def fromJson[E](json: String, clazz: Class[E]): E = MPJson.deserialize(json, clazz).asInstanceOf[E]
}
