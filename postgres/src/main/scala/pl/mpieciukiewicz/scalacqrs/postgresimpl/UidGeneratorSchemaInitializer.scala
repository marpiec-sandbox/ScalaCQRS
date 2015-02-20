package pl.mpieciukiewicz.scalacqrs.postgresimpl

import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs.postgresimpl.JdbcUtils._

class UidGeneratorSchemaInitializer(implicit dbDataSource: DataSource)  {

  def initSchema(): Unit = {
    try {
      executeStatementWithoutParams("CREATE SEQUENCE uids_seq INCREMENT BY 100 START 1000;")
    } catch {
      case e: Exception => () //ignore until CREATE SEQUENCE IF NOT EXISTS is available in PostgreSQL
    }
  }

}
