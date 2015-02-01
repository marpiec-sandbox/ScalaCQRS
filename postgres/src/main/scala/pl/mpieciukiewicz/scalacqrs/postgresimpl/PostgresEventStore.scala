package pl.mpieciukiewicz.scalacqrs.postgresimpl


import java.sql.ResultSet
import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs._

import scala.collection.mutable.ListBuffer


class PostgresEventStore(dbDataSource: DataSource, serializer: ObjectSerializer) extends EventStore {

  val jdbcUtils = new JdbcUtils(dbDataSource)

  import jdbcUtils._

  val SELECT_EVENTS_QUERY = "SELECT command_uid, aggregate_uid, version, event_time, event_type, event" +
    " FROM events" +
    " WHERE aggregate_uid = ?" +
    " ORDER BY version"

  val SELECT_EVENTS_QUERY_TO_VERSION = "SELECT command_uid, aggregate_uid, version, event_time, event_type, event" +
    " FROM events " +
    " WHERE aggregate_uid = ? " +
    " AND version <= ? " +
    " ORDER BY version"

  val SELECT_EVENTS_QUERY_FROM_VERSION = "SELECT command_uid, aggregate_uid, version, event_time, event_type, event" +
    " FROM events" +
    " WHERE aggregate_uid = ?" +
    " AND version >= ?" +
    " ORDER BY version"

  val SELECT_EVENTS_BY_TYPE = "SELECT command_uid, aggregate_uid, version, event, event_type" +
    " FROM events" +
    " WHERE event_type = ?" +
    " ORDER BY version"

  val SELECT_AGGREGATES_IDS = "SELECT uid" +
    " FROM aggregates" +
    " WHERE type = ?"

  val COUNT_ALL_AGGREGATES = "SELECT count(*) " +
    " FROM aggregates" +
    " WHERE type = ?"

  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId): Seq[EventRow[T]] = {

    getQueryResult(SELECT_EVENTS_QUERY) { statement =>
      statement.setLong(1, uid.uid)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  override def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int): Seq[EventRow[T]] = {
    getQueryResult(SELECT_EVENTS_QUERY_FROM_VERSION) { statement =>
      statement.setLong(1, uid.uid)
      statement.setInt(2, fromVersion)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  override def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int): Seq[EventRow[T]] = {
    getQueryResult(SELECT_EVENTS_QUERY_TO_VERSION) { statement =>
      statement.setLong(1, uid.uid)
      statement.setInt(2, toVersion)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  private def getEvents[T](resultSet: ResultSet): Seq[EventRow[T]] = {
    var eventsRows = ListBuffer[EventRow[T]]()
    while (resultSet.next()) {
      val eventRow = EventRow[T](
        CommandId(resultSet.getLong(1)),
        AggregateId(resultSet.getLong(2)),
        resultSet.getInt(3),
        resultSet.getTimestamp(4).toInstant,
        serializer.fromJson(resultSet.getString(6), Class.forName(resultSet.getString(5)).asInstanceOf[Class[Event[T]]]))
      eventsRows += eventRow
    }
    eventsRows.toVector
  }


  override def addCreationEvent(commandId: CommandId, newAggregateId: AggregateId, event: CreationEvent[_]): Unit =
    addEvent(commandId, newAggregateId, 0, event)

  override def addModificationEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: ModificationEvent[_]): Unit =
    addEvent(commandId, aggregateId, expectedVersion, event)

  override def addDeletionEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: DeletionEvent[_]): Unit =
    addEvent(commandId, aggregateId, expectedVersion, event)

  private def addEvent(commandId: CommandId, aggregateId: AggregateId, expectedVersion: Int, event: Event[_]): Unit = {
    executeStatement("SELECT add_event(?, ?, ?, ?, ?, ?);") { statement =>
      statement.setLong(1, commandId.uid)
      statement.setLong(2, aggregateId.uid)
      statement.setInt(3, expectedVersion)
      statement.setString(4, event.aggregateType.getName)
      statement.setString(5, event.getClass.getName)
      statement.setString(6, serializer.toJson(event))
    }
    callEventListeners(aggregateId, event)
  }

  override def getAllAggregateIds[T](aggregateClass: Class[T]): List[AggregateId] = {
    getQueryResult(COUNT_ALL_AGGREGATES) { statement =>
      statement.setString(1, aggregateClass.getName)
    } { resultSet =>
      var ids = List[AggregateId]()
      while (resultSet.next()) {
        ids ::= AggregateId(resultSet.getLong(1))
      }
      ids
    }
  }

  override def countAllAggregates[T](aggregateClass: Class[T]): Long = {
    getQueryResult(COUNT_ALL_AGGREGATES) { statement =>
      statement.setString(1, aggregateClass.getName)
    } { resultSet =>
      resultSet.getLong(1)
    }
  }

}
