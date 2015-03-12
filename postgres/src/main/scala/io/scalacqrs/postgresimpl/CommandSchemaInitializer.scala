package io.scalacqrs.postgresimpl

import javax.sql.DataSource

import io.scalacqrs.postgresimpl.JdbcUtils._

class CommandSchemaInitializer(implicit dbDataSource: DataSource) {

  def initSchema(): Unit = {
    createCommandsTable()
    try {
      createCommandsSequence()
    } catch {
      case e: Exception => () //ignore until CREATE SEQUENCE IF NOT EXISTS is available in PostgreSQL
    }
  }

  private def createCommandsTable(): Unit = {
    executeStatementWithoutParams(
      """
        |CREATE TABLE IF NOT EXISTS commands (
        |  id INT NOT NULL PRIMARY KEY,
        |  command_id BIGINT NOT NULL,
        |  user_id BIGINT NOT NULL,
        |  command_time TIMESTAMP NOT NULL,
        |  command_type VARCHAR(128) NOT NULL,
        |  command_type_version INT NOT NULL,
        |  command VARCHAR(10240) NOT NULL)
      """.stripMargin)
  }

  private def createCommandsSequence(): Unit = {
    executeStatementWithoutParams("CREATE SEQUENCE commands_seq")
  }

}
