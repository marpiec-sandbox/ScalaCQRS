package pl.mpieciukiewicz.scalacqrs.jdbcimpl

import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs.{UID, UIDGenerator}

class JdbcUIDGenerator(dbDataSource: DataSource) extends UIDGenerator {

  val query = "SELECT last_value, NEXTVAL('uids_seq') FROM uids_seq"

  var current = 0L
  var maximum = -1L

  override def nextUID: UID = synchronized {
    if(current > maximum) {
      val connection = dbDataSource.getConnection
      val resultSet = connection.prepareStatement(query).executeQuery()
      if (resultSet.next()) {
        current = resultSet.getLong(1)
        maximum = resultSet.getLong(2) - 1
      } else {
        throw new IllegalStateException("Query returned no values, that should not happen.")
      }
    } else {
      current += 1
    }
    UID(current)

  }
}
