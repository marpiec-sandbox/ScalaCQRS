package pl.mpieciukiewicz.scalacqrs.jdbcimpl

import java.sql.Connection
import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs.{UID, UIDGenerator}

class JdbcUIDGenerator(dbDataSource: DataSource) extends UIDGenerator {

  val NEXT_VAL_QUERY = "SELECT NEXTVAL('uids_seq')"
  val SEQUENCE_STEP_QUERY = "SELECT increment_by FROM uids_seq"

  val stepMinusOne = loadSequenceStep(dbDataSource.getConnection) - 1
  var previousValue = 0L
  var maximum = 0L

  override def nextUID: UID = synchronized {
    if(previousValue == maximum) {
      val connection = dbDataSource.getConnection
      try {
        val resultSet = connection.prepareStatement(NEXT_VAL_QUERY).executeQuery()
        if (resultSet.next()) {
          previousValue = resultSet.getLong(1)
          maximum = previousValue + stepMinusOne
        } else {
          throw new IllegalStateException("Query returned no values, that should not happen.")
        }
      } finally {
        connection.close()
      }
    } else {
      previousValue += 1
    }
    UID(previousValue)

  }

  private def loadSequenceStep(connection: Connection):Long = {
    val resultSet = connection.prepareStatement(SEQUENCE_STEP_QUERY).executeQuery()
    val result = if (resultSet.next()) {
      resultSet.getLong(1)
    } else {
      throw new IllegalStateException("Query returned no values, that should not happen.")
    }
    connection.close()
    result
  }
}
