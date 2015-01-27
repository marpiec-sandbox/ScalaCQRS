package pl.mpieciukiewicz.scalacqrs.postgresimpl


import java.sql.{Connection, PreparedStatement}
import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.internal.Event


class PostgresEventStore(dbDataSource: DataSource, serializer: ObjectSerializer) extends EventStore {

  val SELECT_EVENTS_QUERY = "SELECT command_uid, aggregate_uid, version, event_time, event_type, event " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "ORDER BY version"

  val SELECT_EVENTS_QUERY_TO_VERSION = "SELECT command_uid, aggregate_uid, version, event_time, event_type, event " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "AND version <= ? " +
    "ORDER BY version"

  val SELECT_EVENTS_QUERY_FROM_VERSION = "SELECT command_uid, aggregate_uid, version, event_time, event_type, event " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "AND version >= ? " +
    "ORDER BY version"

  val SELECT_EVENTS_BY_TYPE = "SELECT command_uid, aggregate_uid, version, event, event_type " +
    "FROM events " +
    "WHERE event_type = ? " +
    "ORDER BY version"


  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): List[EventRow[T]] = {

    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_EVENTS_QUERY)
    statement.setLong(1, uid.uid)
    val events: List[EventRow[T]] = getEvents(connection, statement)
    statement.close()
    connection.close()
    events
  }

  override def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): List[EventRow[T]] = {
    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_EVENTS_QUERY_FROM_VERSION)
    statement.setLong(1, uid.uid)
    statement.setInt(2, fromVersion)
    val events: List[EventRow[T]] = getEvents(connection, statement)
    statement.close()
    connection.close()
    events
  }

  override def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): List[EventRow[T]] = {
    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_EVENTS_QUERY_TO_VERSION)
    statement.setLong(1, uid.uid)
    statement.setLong(2, toVersion)
    val events: List[EventRow[T]] = getEvents(connection, statement)
    statement.close()
    connection.close()
    events
  }


  private def getEvents[T](connection: Connection, statement: PreparedStatement): List[EventRow[T]] = {
    val resultSet = statement.executeQuery()

    var eventsRows = List[EventRow[T]]()
    while (resultSet.next()) {
      val eventRow = EventRow[T](
        CommandId(resultSet.getLong(1)),
        AggregateId(resultSet.getLong(2)),
        resultSet.getInt(3),
        resultSet.getTimestamp(4).toInstant,
        serializer.fromJson(resultSet.getString(6), Class.forName(resultSet.getString(5)).asInstanceOf[Class[Event[T]]]))
      eventsRows ::= eventRow
    }

    statement.close()
    connection.close()
    eventsRows.reverse
  }


  override def addCreationEvent[T](commandId: CommandId, newAggregateId: AggregateId, event: CreationEvent[T]): Unit =
    addEvent(commandId, newAggregateId, 0, event)

  override def addModificationEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: ModificationEvent[T]): Unit =
    addEvent(commandId, aggregateId, expectedVersion, event)

  override def addDeletionEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: DeletionEvent[T]): Unit =
    addEvent(commandId, aggregateId, expectedVersion, event)

  private def addEvent[T](commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: Event[T]): Unit = {

    val query = "SELECT add_event(?, ?, ?, ?, ?, ?);"

    val connection = dbDataSource.getConnection

    val statement = connection.prepareStatement(query)

    statement.setLong(1, commandId.uid)
    statement.setLong(2, aggregateId.uid)
    statement.setInt(3, expectedVersion)
    statement.setString(4, event.entityClass.getName)
    statement.setString(5, event.getClass.getName)
    statement.setString(6, serializer.toJson(event))

    statement.execute()

    statement.close()
  }

}
