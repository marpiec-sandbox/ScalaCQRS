package pl.mpieciukiewicz.scalacqrs.postgresimpl

import java.sql.{ResultSet, PreparedStatement}
import javax.sql.DataSource

object JdbcUtils {

  def getQueryResult[T](query: String)(prepareStatement: (PreparedStatement) => Unit)(block: (ResultSet) => T)(implicit dbDataSource: DataSource): T = {
    val connection = dbDataSource.getConnection
    try {
      val statement = connection.prepareStatement(query)
      prepareStatement(statement)
      try {
        val resultSet = statement.executeQuery()
        try {
          block(resultSet)
        } finally {
          resultSet.close()
        }
      } finally {
        statement.close()
      }
    } finally {
      connection.close()
    }
  }

  def executeStatement[T](query: String)(prepareStatement: (PreparedStatement) => Unit)(implicit dbDataSource: DataSource): Unit = {
    val connection = dbDataSource.getConnection
    try {
      val statement = connection.prepareStatement(query)
      prepareStatement(statement)
      try {
        statement.execute()
      } finally {
        statement.close()
      }
    } finally {
      connection.close()
    }
  }

  def executeStatementWithoutParams[T](query: String)(implicit dbDataSource: DataSource): Unit = {
    val connection = dbDataSource.getConnection
    try {
      val statement = connection.prepareStatement(query)
      try {
        statement.execute()
      } finally {
        statement.close()
      }
    } finally {
      connection.close()
    }
  }
}
