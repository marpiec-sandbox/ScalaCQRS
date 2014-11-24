package pl.mpieciukiewicz.scalacqrs.jdbcimpl

import pl.mpieciukiewicz.scalacqrs.{UID, UIDGenerator}

class JdbcUIDGenerator extends UIDGenerator {
  override def nextUID: UID = ???
}
