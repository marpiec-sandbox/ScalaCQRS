package io.testdomain.postgresimpl.jdbc

import org.apache.commons.dbcp2.BasicDataSource


object ConnectionPoolFactory {

  def createSampleConnectionPool: BasicDataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("scalacqrs")
    connectionPool.setPassword("scalacqrs")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl("jdbc:postgresql://localhost:5432/scalacqrs")
    connectionPool.setInitialSize(3)
    connectionPool.setMaxTotal(5)
    connectionPool
  }
  

}
