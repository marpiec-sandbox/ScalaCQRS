package pl.mpieciukiewicz.scalacqrs.jdbcimpl


import java.{lang, io}
import java.util.Date

import pl.marpiec.cqrs.exception.ConcurrentAggregateModificationException
import pl.mpieciukiewicz.scalacqrs.exception.ConcurrentAggregateModificationException

import scala.collection.JavaConversions._

import java.sql.{Timestamp, ResultSet}
import java.time.Instant

import org.springframework.jdbc.core.{RowMapper, JdbcTemplate}
import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.internal.Event

class JdbcEventStore(jdbcTemplate: JdbcTemplate, serializer: EventSerializer) extends EventStore {

  val SELECT_EVENTS_QUERY = "SELECT user_uid, aggregate_uid, version, event, event_type, event_time " +
    "FROM events " +
    "WHERE aggregate_uid = ? " +
    "ORDER BY version"

  val SELECT_EVENTS_BY_TYPE = "SELECT user_uid, aggregate_uid, version, event, event_type " +
    "FROM events " +
    "WHERE event_type = ? " +
    "ORDER BY version"

  val eventRowRowMapper = new RowMapper[EventRow[_]] {
    def mapRow(resultSet: ResultSet, rowNum: Int) = {
      val userId = resultSet.getLong(1)
      val aggregateId = resultSet.getLong(2)
      val version = resultSet.getInt(3)
      val event = resultSet.getString(4)
      val eventType = resultSet.getString(5)
      val eventTime = new Instant(resultSet.getTimestamp(6))
      new EventRow(new UID(userId), new UID(aggregateId), version, eventTime,
        serializer.fromJson(event, Class.forName(eventType)).asInstanceOf[Event[_]])
    }
  }





  override def addCreationEvent[T](userId: UID, newAggregateId: UID, event: CreationEvent[T]): Unit = ???

  override def getEventsForAggregate[T](aggregateClass: Class[T], uid: UID): List[EventRow[T]] = {
    jdbcTemplate.query(SELECT_EVENTS_QUERY, eventRowRowMapper, Array(Long.box(uid.uid))).toList.asInstanceOf[List[EventRow[T]]]
  }

  override def getEventsForAggregateFromVersion[T](aggregateClass: Class[T], uid: UID, fromVersion: Int): List[EventRow[T]] = ???

  override def addModificationEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: ModificationEvent[T]): Unit = ???

  override def addDeletionEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: DeletionEvent[T]): Unit = ???

  override def getEventsForAggregateToVersion[T](aggregateClass: Class[T], uid: UID, toVersion: Int): List[EventRow[T]] = ???

  private def addEvent[T](userId: UID, aggregateId: UID, expectedVersion: Int, event: Event[T]): Unit = {

    val currentVersion = jdbcTemplate.queryForObject(
      "SELECT version FROM aggregates WHERE uid = ? AND class = ?",
      Array(Long.box(aggregateId.uid), event.entityClass.getName).asInstanceOf[Array[AnyRef]],
      classOf[Int])

    if (currentVersion == 0) {
      throw new IllegalStateException("No aggregate found! ")
    }


    if (currentVersion != expectedVersion) {
      throw new ConcurrentAggregateModificationException("Expected " + expectedVersion + " but is " + currentVersion)
    }


    jdbcTemplate.update("INSERT INTO events (id, user_uid, aggregate_uid, event_time, version, event_type, event) " +
      "VALUES (NEXTVAL('events_seq'), ?, ?, ?, ?, ?, ?)",
      Array(Long.box(userId.uid), Long.box(aggregateId.uid),
        new Timestamp(new Date().getTime), Int.box(expectedVersion), event.getClass.getName,
        serializer.toJson(event)): _*)

    jdbcTemplate.update("UPDATE aggregates SET version = version + 1 WHERE uid = ?",
      Array(Long.box(aggregateId.uid)): _*)

  }

}
