package io.scalacqrs.postgresimpl

import javax.sql.DataSource

import io.scalacqrs.postgresimpl.JdbcUtils._

class EventSchemaInitializer(implicit dbDataSource: DataSource)  {

  def initSchema(): Unit = {
    createEventsTable()
    createAggregatesTable()
    createNoopEventTable()
    try {
      createEventsSequence()
    } catch {
      case e: Exception => () //ignore until CREATE SEQUENCE IF NOT EXISTS is available in PostgreSQL
    }
    createAddEventFunction()
    createAddUndoEventFunction()
  }


  private def createEventsTable() = {
    executeStatementWithoutParams(
      """
        |CREATE TABLE IF NOT EXISTS events (
        |  id INT NOT NULL PRIMARY KEY,
        |  command_id BIGINT NOT NULL,
        |  user_id BIGINT NOT NULL,
        |  aggregate_id BIGINT NOT NULL,
        |  event_time TIMESTAMP NOT NULL,
        |  version INT NOT NULL,
        |  event_type VARCHAR(128) NOT NULL,
        |  event_type_version INT NOT NULL,
        |  event VARCHAR(10240) NOT NULL)
      """.stripMargin)
  }

  private def createAggregatesTable() = {
    executeStatementWithoutParams(
      """
        |CREATE TABLE IF NOT EXISTS aggregates (
        |  id BIGINT NOT NULL PRIMARY KEY,
        |  type VARCHAR(128) NOT NULL,
        |  version INT NOT NULL);
      """.stripMargin)
  }

  private def createNoopEventTable() {
    executeStatementWithoutParams(
      """
        |CREATE TABLE IF NOT EXISTS noop_events (
        |  id INT NOT NULL PRIMARY KEY,
        |  from_version INT NOT NULL)
      """.stripMargin)
  }

  private def createEventsSequence() = {
    executeStatementWithoutParams("CREATE SEQUENCE events_seq")
  }

  private def createAddEventFunction(): Unit = {
    executeStatementWithoutParams(
      """
        |CREATE OR REPLACE FUNCTION add_event(command_id bigint, user_id bigint, aggregate_id bigint, expected_version INT, aggregate_type VARCHAR(128), event_type VARCHAR(128), event_type_version INT, event VARCHAR(10240))
        |RETURNS void AS
        |$$
        |DECLARE
        |    current_version int;
        |BEGIN
        | SELECT aggregates.version INTO current_version from aggregates where id = aggregate_id;
        |    IF NOT FOUND THEN
        |        IF expected_version = 0 THEN
        |            INSERT INTO AGGREGATES (id, type, version) VALUES (aggregate_id, aggregate_type, 0);
        |            current_version := 0;
        |        ELSE
        |	    RAISE EXCEPTION 'aggregate not found, id %, aggregate_type %', aggregate_id, aggregate_type;
        |        END IF;
        |    END IF;
        |    IF current_version != expected_version THEN
        |	RAISE EXCEPTION 'Concurrent aggregate modification exception, command id %, user id %, aggregate id %, expected version %, current_version %, event_type %, event_type_version %, event %', command_id, user_id, aggregate_id, expected_version, current_version, event_type, event_type_version, event;
        |    END IF;
        |    INSERT INTO events (id, command_id, user_id, aggregate_id, event_time, version, event_type, event_type_version, event) VALUES (NEXTVAL('events_seq'), command_id, user_id, aggregate_id, current_timestamp, current_version + 1, event_type, event_type_version, event);
        |    UPDATE aggregates SET version = current_version + 1 WHERE id = aggregate_id;
        |END;
        |$$
        |LANGUAGE 'plpgsql' VOLATILE;
      """.stripMargin)
  }


  private def createAddUndoEventFunction(): Unit = {
    executeStatementWithoutParams(
      """
        |CREATE OR REPLACE FUNCTION add_undo_event(command_id bigint, user_id bigint, aggregate_id bigint, expected_version INT, aggregate_type VARCHAR(128), event_type VARCHAR(128), event_type_version INT, event VARCHAR(10240), undo_count INT)
        |RETURNS void AS
        |$$
        |DECLARE
        |    current_version int;
        |BEGIN
        | SELECT aggregates.version INTO current_version from aggregates where id = aggregate_id;
        |    IF NOT FOUND THEN
        |        IF expected_version = 0 THEN
        |            RAISE EXCEPTION 'Cannot undo event for non existing aggregate';
        |        ELSE
        |    	    RAISE EXCEPTION 'aggregate not found, id %, aggregate_type %', aggregate_id, aggregate_type;
        |        END IF;
        |    END IF;
        |    IF current_version != expected_version THEN
        |	RAISE EXCEPTION 'Concurrent aggregate modification exception, command id %, user id %, aggregate id %, expected version %, current_version %, event_type %, event_type_version %, event %', command_id, user_id, aggregate_id, expected_version, current_version, event_type, event_type_version, event;
        |    END IF;
        |    INSERT INTO noop_events (id, from_version) (select events.id, current_version + 1
        |     from events
        |     left join undos on events.id = undos.id
        |     where undos.id is null order by events.version desc limit undo_count)
        |    INSERT INTO noop_events(id, from_version) VALUES (CURRVAL(events_seq), current_version + 1);
        |    INSERT INTO events (id, command_id, user_id, aggregate_id, event_time, version, event_type, event_type_version, event) VALUES (NEXTVAL('events_seq'), command_id, user_id, aggregate_id, current_timestamp, current_version + 1, event_type, event_type_version, event);
        |    UPDATE aggregates SET version = current_version + 1 WHERE id = aggregate_id;
        |END;
        |$$
        |LANGUAGE 'plpgsql' VOLATILE;
      """.stripMargin)
  }


}
