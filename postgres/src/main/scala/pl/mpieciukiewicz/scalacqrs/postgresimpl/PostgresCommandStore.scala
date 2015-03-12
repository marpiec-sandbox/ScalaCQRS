package pl.mpieciukiewicz.scalacqrs.postgresimpl

import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.command.{Command, CommandRow}
import pl.mpieciukiewicz.scalacqrs.data.UserId
import JdbcUtils._

class PostgresCommandStore(dbDataSource: DataSource, serializer: ObjectSerializer) extends CommandStore {

  implicit private val db = dbDataSource

  val INSERT_COMMAND = "INSERT INTO commands(id, command_id, user_id, command_time, command_type, command_type_version, command)" +
    " VALUES (NEXTVAL('commands_seq'), ?, ?, current_timestamp, ?, ?, ?)"

  val SELECT_COMMAND_BY_ID = "SELECT command_id, user_id, command_time, command_type, command_type_version, command" +
    " FROM commands" +
    " WHERE command_id = ?"

  new CommandSchemaInitializer().initSchema()

  override def addCommand(commandId: CommandId, userId: UserId, command: Command[_]): Unit = {
    executeStatement(INSERT_COMMAND) {statement =>
      statement.setLong(1, commandId.uid)
      statement.setLong(2, userId.uid)
      statement.setString(3, command.getClass.getName)
      statement.setInt(4, 0)
      statement.setString(5, serializer.toJson(command))
    }
  }

  override def getCommandById(commandId: CommandId): CommandRow = {
    getQueryResult(SELECT_COMMAND_BY_ID) {statement =>
      statement.setLong(1, commandId.uid)
    } { resultSet =>
      if (resultSet.next()) {
        CommandRow(
          CommandId(resultSet.getLong(1)),
          UserId(resultSet.getLong(2)),
          resultSet.getTimestamp(3).toInstant,
          serializer.fromJson(resultSet.getString(6), Class.forName(resultSet.getString(4)).asInstanceOf[Class[Command[_]]]))
      } else {
        throw new IllegalStateException("Command not found " + commandId)
      }
    }
  }

}
