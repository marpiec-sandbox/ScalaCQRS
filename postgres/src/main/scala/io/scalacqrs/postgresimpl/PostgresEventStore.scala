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


  override def getEventsForAggregate[A: TypeTag](uid: AggregateId): Seq[EventRow[Event[A]]] = {

    getQueryResult(SELECT_EVENTS_QUERY) { statement =>
      statement.setLong(1, uid.uid)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  override def getEventsForAggregateFromVersion[A: TypeTag](uid: AggregateId, fromVersion: Int): Seq[EventRow[Event[A]]] = {
    getQueryResult(SELECT_EVENTS_QUERY_FROM_VERSION) { statement =>
      statement.setLong(1, uid.uid)
      statement.setInt(2, fromVersion)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  override def getEventsForAggregateToVersion[A: TypeTag](uid: AggregateId, toVersion: Int): Seq[EventRow[Event[A]]] = {
    getQueryResult(SELECT_EVENTS_QUERY_TO_VERSION) { statement =>
      statement.setLong(1, uid.uid)
      statement.setInt(2, toVersion)
    } { resultSet =>
      getEvents(resultSet)
    }
  }

  private def getEvents[A](resultSet: ResultSet)
                          (implicit tag: TypeTag[A]): Seq[EventRow[Event[A]]] = {
    var eventsRows = ListBuffer[EventRow[Event[A]]]()
    while (resultSet.next()) {
      val eventRow = EventRow[Event[A]](
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


  override def addFirstEvent[E <: Event[_]: TypeTag](commandId: CommandId, userId: UserId, newAggregateId: AggregateId, event: E): Unit =
    addEvent(commandId, userId, newAggregateId, 0, event)


  override def addEvent[E <: Event[_]: TypeTag](commandId: CommandId, userId: UserId, aggregateId: AggregateId,
                        expectedVersion: Int, event: E): Unit = {
    executeStatement("SELECT add_event(?, ?, ?, ?, ?, ?, ?, ?);") { statement =>
      statement.setLong(1, commandId.uid)
      statement.setLong(2, userId.uid)
      statement.setLong(3, aggregateId.uid)
      statement.setInt(4, expectedVersion)
      statement.setString(5, event.aggregateType.typeSymbol.fullName)
      statement.setString(6, event.getClass.getName)
      statement.setInt(7, 0)
      statement.setString(8, serializer.toJson[E](event))
    }
    callUpdateListeners(aggregateId, expectedVersion + 1, event)
  }

  override def getAllAggregateIds[A: TypeTag]: List[AggregateId] = {
    getQueryResult(COUNT_ALL_AGGREGATES) { statement =>
      statement.setString(1, typeOf[A].typeSymbol.fullName)
    } { resultSet =>
      var ids = List[AggregateId]()
      while (resultSet.next()) {
        ids ::= AggregateId(resultSet.getLong(1))
      }
      ids
    }
  }

  override def countAllAggregates[A: TypeTag]: Long = {
    getQueryResult(COUNT_ALL_AGGREGATES) { statement =>
      statement.setString(1, typeOf[A].typeSymbol.fullName)
    } { resultSet =>
      resultSet.getLong(1)
    }
  }


}
