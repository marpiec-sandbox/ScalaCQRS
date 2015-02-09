package pl.mpieciukiewicz.scalacqrs.postgresimpl

import java.sql.{PreparedStatement, Connection}
import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.data.AggregateId

class PostgresUidGenerator(dbDataSource: DataSource) extends UIDGenerator {

  val NEXT_VAL_QUERY = "SELECT NEXTVAL('uids_seq')"
  val SEQUENCE_STEP_QUERY = "SELECT increment_by FROM uids_seq"

  val stepMinusOne = loadSequenceStep(dbDataSource.getConnection) - 1
  var previousValue = 0L
  var maximum = 0L

  override def nextAggregateId = synchronized {
    AggregateId(nextUniqueValue)
  }

  override def nextCommandId = synchronized {
    CommandId(nextUniqueValue)
  }
  
  private def nextUniqueValue: Long = {
    if (previousValue == maximum) {
      val connection = dbDataSource.getConnection
      try {
        val statement = connection.prepareStatement(NEXT_VAL_QUERY)
        try {
          val resultSet = statement.executeQuery()
          if (resultSet.next()) {
            previousValue = resultSet.getLong(1)
            maximum = previousValue + stepMinusOne
          } else {
            throw new IllegalStateException("Query returned no values, that should not happen.")
          }
        } finally {
          statement.close()
        }
      } finally {
        connection.close()
      }
    } else {
      previousValue += 1
    }
    previousValue
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
