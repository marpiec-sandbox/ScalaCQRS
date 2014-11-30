package pl.mpieciukiewicz.jdbs

import javax.sql.DataSource

import org.apache.commons.dbcp2.BasicDataSource


object ConnectionPoolFactory {

  def createConnectionPool: DataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("eventstore")
    connectionPool.setPassword("eventstore")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl("jdbc:postgresql://localhost:5432/eventstore")
    connectionPool.setInitialSize(5)
    connectionPool
  }

}
