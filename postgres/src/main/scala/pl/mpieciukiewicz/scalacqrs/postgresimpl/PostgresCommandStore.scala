package pl.mpieciukiewicz.scalacqrs.postgresimpl

import java.sql.{PreparedStatement, Connection}
import javax.sql.DataSource

import pl.mpieciukiewicz.scalacqrs._
import pl.mpieciukiewicz.scalacqrs.command.CommandRow
import pl.mpieciukiewicz.scalacqrs.data.UserId

class PostgresCommandStore(dbDataSource: DataSource, serializer: ObjectSerializer) extends CommandStore {

  val INSERT_COMMAND = "INSERT INTO commands(id, command_id, user_id, command_time, command_type, command_type_version, command)" +
    " VALUES (NEXTVAL('commands_seq'), ?, ?, current_timestamp, ?, ?, ?)"

  val SELECT_COMMAND_BY_ID = "SELECT command_id, user_id, command_time, command_type, command_type_version, command" +
    " FROM commands" +
    " WHERE command_uid = ?"

  override def addCommand(commandId: CommandId, userId: UserId, command: AnyRef): Unit = {
    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(INSERT_COMMAND)
    statement.setLong(1, commandId.uid)
    statement.setLong(2, userId.uid)
    statement.setString(3, command.getClass.getName)
    statement.setInt(4, 0)
    statement.setString(5, serializer.toJson(command))
    statement.execute()
    statement.close()
    connection.close()
  }

  override def getCommandById(commandId: CommandId): CommandRow = {
    val connection = dbDataSource.getConnection
    val statement = connection.prepareStatement(SELECT_COMMAND_BY_ID)
    statement.setLong(1, commandId.uid)
    val command = getCommands(connection, statement).head
    statement.close()
    connection.close()
    command
  }

  private def getCommands[T](connection: Connection, statement: PreparedStatement): List[CommandRow] = {
    val resultSet = statement.executeQuery()

    var eventsRows = List[CommandRow]()
    while (resultSet.next()) {
      val commandRow = CommandRow(
        CommandId(resultSet.getLong(1)),
        UserId(resultSet.getLong(2)),
        resultSet.getTimestamp(3).toInstant,
        serializer.fromJson(resultSet.getString(6), Class.forName(resultSet.getString(4)).asInstanceOf[Class[AnyRef]]))
      eventsRows ::= commandRow
    }

    statement.close()
    connection.close()
    eventsRows.reverse
  }

}
