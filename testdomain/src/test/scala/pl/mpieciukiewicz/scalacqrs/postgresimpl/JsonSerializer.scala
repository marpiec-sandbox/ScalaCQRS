package pl.mpieciukiewicz.scalacqrs.postgresimpl

import pl.mpieciukiewicz.mpjsons.MPJson

class JsonSerializer extends ObjectSerializer {

  override def toJson(obj: AnyRef): String = MPJson.serialize(obj)

  override def fromJson[E](json: String, clazz: Class[E]): E = MPJson.deserialize(json, clazz).asInstanceOf[E]
}
