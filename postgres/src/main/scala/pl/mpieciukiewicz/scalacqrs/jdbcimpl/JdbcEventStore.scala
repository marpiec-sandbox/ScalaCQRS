package pl.mpieciukiewicz.scalacqrs.jdbcimpl


import java.util.Date
import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs.exception.ConcurrentAggregateModificationException


import java.sql.{PreparedStatement, Connection, Timestamp, ResultSet}
import java.time.Instant

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.internal.Event


class JdbcEventStore(dbDataSource: DataSource, serializer: EventSerializer) extends EventStore {

  val SELECT_EVENTS_QUERY = "SELECT user_uid, aggregate_uid, version, event_time, event_type, event " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "ORDER BY version"

  val SELECT_EVENTS_QUERY_TO_VERSION = "SELECT user_uid, aggregate_uid, version, event_time, event_type, event " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "AND version <= ? " +
    "ORDER BY version"

  val SELECT_EVENTS_QUERY_FROM_VERSION = "SELECT user_uid, aggregate_uid, version, event_time, event_type, event " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "AND version >= ? " +
    "ORDER BY version"

  val SELECT_EVENTS_BY_TYPE = "SELECT user_uid, aggregate_uid, version, event, event_type " +
    "FROM events " +
    "WHERE event_type = ? " +
    "ORDER BY version"


  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: UID): List[EventRow[T]] = {

    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_EVENTS_QUERY)
    statement.setLong(1, uid.uid)
    getEvents(connection, statement)
  }

  override def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: UID, fromVersion: Int): List[EventRow[T]] = {
    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_EVENTS_QUERY_FROM_VERSION)
    statement.setLong(1, uid.uid)
    statement.setInt(2, fromVersion)
    getEvents(connection, statement)
  }

  override def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: UID, toVersion: Int): List[EventRow[T]] = {
    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_EVENTS_QUERY_TO_VERSION)
    statement.setLong(1, uid.uid)
    statement.setLong(2, toVersion)
    getEvents(connection, statement)
  }


  private def getEvents[T](connection: Connection, statement: PreparedStatement): List[EventRow[T]] = {
    val resultSet = statement.executeQuery()

    var eventsRows = List[EventRow[T]]()
    while (resultSet.next()) {
      val eventRow = EventRow[T](
        UID(resultSet.getLong(1)),
        UID(resultSet.getLong(2)),
        resultSet.getInt(3),
        resultSet.getTimestamp(4).toInstant,
        serializer.fromJson(resultSet.getString(6), Class.forName(resultSet.getString(5)).asInstanceOf[Class[Event[T]]]))
      eventsRows ::= eventRow
    }

    statement.close()
    connection.close()
    eventsRows.reverse
  }


  override def addCreationEvent[T](userId: UID, newAggregateId: UID, event: CreationEvent[T]): Unit =
    addEvent(userId, newAggregateId, 0, event)

  override def addModificationEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: ModificationEvent[T]): Unit =
    addEvent(userId, aggregateId, expectedVersion, event)

  override def addDeletionEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: DeletionEvent[T]): Unit =
    addEvent(userId, aggregateId, expectedVersion, event)

  private def addEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: Event[T]): Unit = {

    val query = "SELECT add_event(?, ?, ?, ?, ?, ?);"

    val connection = dbDataSource.getConnection

    val statement = connection.prepareStatement(query)

    statement.setLong(1, userId.uid)
    statement.setLong(2, aggregateId.uid)
    statement.setInt(3, expectedVersion)
    statement.setString(4, event.entityClass.getName)
    statement.setString(5, event.getClass.getName)
    statement.setString(6, serializer.toJson(event))

    statement.execute()

    statement.close()
  }

}
