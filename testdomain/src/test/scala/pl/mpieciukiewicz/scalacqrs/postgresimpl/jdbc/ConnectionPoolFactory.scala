package pl.mpieciukiewicz.scalacqrs.postgresimpl.jdbc

import javax.sql.DataSource

import org.apache.commons.dbcp2.BasicDataSource


object ConnectionPoolFactory {

  def createEventStoreConnectionPool: BasicDataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("eventstore")
    connectionPool.setPassword("eventstore")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl("jdbc:postgresql://localhost:5432/eventstore")
    connectionPool.setInitialSize(3)
    connectionPool.setMaxTotal(5)
    connectionPool
  }

  def createCommandStoreConnectionPool: BasicDataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("commandstore")
    connectionPool.setPassword("commandstore")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl("jdbc:postgresql://localhost:5432/commandstore")
    connectionPool.setInitialSize(3)
    connectionPool.setMaxTotal(5)
    connectionPool
  }

}
