CREATE TABLE IF NOT EXISTS events (
  id INT NOT NULL PRIMARY KEY,
  user_uid BIGINT NOT NULL,
  aggregate_uid BIGINT NOT NULL,
  event_time TIMESTAMP NOT NULL,
  version INT NOT NULL,
  event_type VARCHAR(128) NOT NULL,
  event VARCHAR(10240) NOT NULL);

CREATE TABLE IF NOT EXISTS aggregates (
  id INT NOT NULL PRIMARY KEY,
  class VARCHAR(128) NOT NULL,
  uid BIGINT NOT NULL,
  version INT NOT NULL);

CREATE TABLE IF NOT EXISTS uids (
  id INT NOT NULL PRIMARY KEY,
  uidName VARCHAR(128) NOT NULL,
  uid BIGINT NOT NULL);

CREATE SEQUENCE IF NOT EXISTS events_seq;
CREATE SEQUENCE IF NOT EXISTS aggregates_seq;
CREATE SEQUENCE IF NOT EXISTS uids_seq;