package io.scalacqrs.postgresimpl


import java.sql.ResultSet
import javax.sql.DataSource

import io.scalacqrs._
import io.scalacqrs.data.{UserId, AggregateId}
import io.scalacqrs.event.{EventRow, Event}

import scala.collection.mutable.ListBuffer
import JdbcUtils._

import scala.reflect.runtime.universe._

class PostgresEventStore(dbDataSource: DataSource, serializer: ObjectSerializer) extends EventStore {

  implicit private val db = dbDataSource

  new EventSchemaInitializer().initSchema()

  val SELECT_EVENTS_QUERY = "SELECT command_id, user_id, aggregate_id, version, event_time, event_type, event" +
    " FROM events" +
    " WHERE aggregate_id = ?" +
    " ORDER BY version"

  val SELECT_EVENTS_QUERY_TO_VERSION = "SELECT command_id, user_id, aggregate_id, version, event_time, event_type, event" +
    " FROM events " +
    " WHERE aggregate_id = ? " +
    " AND version <= ? " +
    " ORDER BY version"

  val SELECT_EVENTS_QUERY_FROM_VERSION = "SELECT command_id, user_id, aggregate_id, version, event_time, event_type, event" +
    " FROM events" +
    " WHERE aggregate_id = ?" +
    " AND version >= ?" +
    " ORDER BY version"

  val SELECT_EVENTS_BY_TYPE = "SELECT command_id, user_id, aggregate_id, version, event, event_type" +
    " FROM events" +
    " WHERE event_type = ?" +
    " ORDER BY version"

  val SELECT_AGGREGATES_IDS = "SELECT id" +
    " FROM aggregates" +
    " WHERE type = ?"

  val COUNT_ALL_AGGREGATES = "SELECT count(*) " +
    " FROM aggregates" +
    " WHERE type = ?"


  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: AggregateId)
                                       (implicit tag: TypeTag[T]): Seq[EventRow[T]] = {

    getQueryResult(SELECT_EVENTS_QUERY) { statement =>
      statement.setLong(1, uid.uid)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  override def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: AggregateId, fromVersion: Int)
                                                  (implicit tag: TypeTag[T]): Seq[EventRow[T]] = {
    getQueryResult(SELECT_EVENTS_QUERY_FROM_VERSION) { statement =>
      statement.setLong(1, uid.uid)
      statement.setInt(2, fromVersion)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  override def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: AggregateId, toVersion: Int)
                                                (implicit tag: TypeTag[T]): Seq[EventRow[T]] = {
    getQueryResult(SELECT_EVENTS_QUERY_TO_VERSION) { statement =>
      statement.setLong(1, uid.uid)
      statement.setInt(2, toVersion)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  private def getEvents[T](resultSet: ResultSet)
                          (implicit tag: TypeTag[T]): Seq[EventRow[T]] = {
    var eventsRows = ListBuffer[EventRow[T]]()
    while (resultSet.next()) {
      val eventRow = EventRow[T](
        CommandId(resultSet.getLong(1)),
        UserId(resultSet.getLong(2)),
        AggregateId(resultSet.getLong(3)),
        resultSet.getInt(4),
        resultSet.getTimestamp(5).toInstant,
        serializer.fromJson(resultSet.getString(7), resultSet.getString(6)))
      eventsRows += eventRow
    }
    eventsRows.toVector
  }


  override def addFirstEvent[A: TypeTag, E <: Event[A]: TypeTag](commandId: CommandId, userId: UserId, newAggregateId: AggregateId, event: E): Unit =
    addEvent[A, E](commandId, userId, newAggregateId, 0, event)


  override def addEvent[A: TypeTag, E <: Event[A]: TypeTag](commandId: CommandId, userId: UserId, aggregateId: AggregateId,
                        expectedVersion: Int, event: E): Unit = {
    executeStatement("SELECT add_event(?, ?, ?, ?, ?, ?, ?, ?);") { statement =>
      statement.setLong(1, commandId.uid)
      statement.setLong(2, userId.uid)
      statement.setLong(3, aggregateId.uid)
      statement.setInt(4, expectedVersion)
      statement.setString(5, event.aggregateType.getName)
      statement.setString(6, event.getClass.getName)
      statement.setInt(7, 0)
      statement.setString(8, serializer.toJson[E](event))
    }
    callUpdateListeners[A](aggregateId, expectedVersion + 1, event)
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
