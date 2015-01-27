package pl.mpieciukiewicz.jdbs

import javax.sql.DataSource

import org.apache.commons.dbcp2.BasicDataSource


object ConnectionPoolFactory {

  def createEventStoreConnectionPool: DataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("eventstore")
    connectionPool.setPassword("eventstore")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl("jdbc:postgresql://localhost:5432/eventstore")
    connectionPool.setInitialSize(10)
    connectionPool.setMaxTotal(20)
    connectionPool
  }

  def createCommandStoreConnectionPool: DataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("commandstore")
    connectionPool.setPassword("commandstore")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl("jdbc:postgresql://localhost:5432/commandstore")
    connectionPool.setInitialSize(10)
    connectionPool.setMaxTotal(20)
    connectionPool
  }

}
