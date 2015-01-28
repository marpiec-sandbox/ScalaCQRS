CREATE TABLE IF NOT EXISTS events (
  id INT NOT NULL PRIMARY KEY,
  command_uid BIGINT NOT NULL,
  aggregate_uid BIGINT NOT NULL,
  event_time TIMESTAMP NOT NULL,
  version INT NOT NULL,
  event_type VARCHAR(128) NOT NULL,
  event VARCHAR(10240) NOT NULL);

CREATE TABLE IF NOT EXISTS aggregates (
  uid BIGINT NOT NULL PRIMARY KEY,
  type VARCHAR(128) NOT NULL,
  version INT NOT NULL);

CREATE SEQUENCE events_seq;
CREATE SEQUENCE uids_seq INCREMENT BY 100;

CREATE OR REPLACE FUNCTION add_event(command_uid bigint, aggregate_uid bigint, expected_version INT, aggregate_type VARCHAR(128), event_type VARCHAR(128), event VARCHAR(10240))
RETURNS void AS
$$
DECLARE
    current_version int;
BEGIN
 SELECT aggregates.version INTO current_version from aggregates where uid = aggregate_uid;
    IF NOT FOUND THEN
        IF expected_version = 0 THEN
            INSERT INTO AGGREGATES (uid, type, version) VALUES (aggregate_uid, aggregate_type, 0);
            current_version := 0;
        ELSE
	    RAISE EXCEPTION 'aggregate not found, uid %, aggregate_type %', aggregate_uid, aggregate_type;
        END IF;
    END IF;
    IF current_version != expected_version THEN
	RAISE EXCEPTION 'Concurrent aggregate modification exception, user id %, aggregate id %, expected version %, current_version %, event_type %, event %', command_uid, aggregate_uid, expected_version, current_version, event_type, event;
    END IF;
    INSERT INTO events (id, command_uid, aggregate_uid, event_time, version, event_type, event) VALUES (NEXTVAL('events_seq'), command_uid, aggregate_uid, current_timestamp, current_version + 1, event_type, event);
    UPDATE aggregates SET version = current_version + 1 WHERE uid = aggregate_uid;
END;
$$
LANGUAGE 'plpgsql' VOLATILE;


--SELECT add_event(2, 2, 0, 'User', 'register', 'Marcin');
--SELECT add_event(2, 2, 1, 'User', 'update_name', 'MARS');
--SELECT add_event(2, 2, 1, 'User', 'update_name', 'WENUS');