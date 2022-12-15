package ru.ac.uniyar.database.repository

import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.util.UUID


class UsersDB: BaseTable() {
    private fun isUUID(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: IllegalArgumentException) {
            false
        } catch (e: NullPointerException) {
            false
        }
    }

    @Throws(SQLException::class)
    fun selectUser(userId: String): User? {
        var user: User? = null

        reopenConnection()
        val sqlStatement = "SELECT * FROM users WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, userId)
        statement.executeQuery()

        val result = statement.resultSet
        if (result != null) {
            val columns = result.metaData.columnCount
            while (result.next()) {
                if (isUUID(result.getString(1)) && columns == 5)
                    user = User(
                        UUID.fromString(result.getString(1)),
                        result.getString(2),
                        result.getString(3),
                        result.getBoolean(4),
                        result.getBoolean(5)
                    )
            }
        }
        statement.close()
        return user
    }

    @Throws(SQLException::class)
    fun addUser(user: User) {
        reopenConnection()
        val sqlStatement = "INSERT INTO users (id, name, pass, isAdmin, isTeacher) VALUES (?,?,?,?,?)"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, user.id.toString())
        statement.setString(2, user.name)
        statement.setString(3, user.pass)
        statement.setBoolean(4, user.isAdmin)
        statement.setBoolean(5, user.isTeacher)
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun updateUser(user: User) {
        reopenConnection()
        val sqlStatement = "UPDATE users SET name = ? WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, user.name)
        statement.setString(2, user.id.toString())
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun deleteUser(userId: String) {
        reopenConnection()
        val sqlStatement = "DELETE FROM users WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, userId)
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun initUsers(users: Users) {
        reopenConnection()
        val statement: Statement = connection!!.createStatement()
        statement.executeQuery("SELECT * FROM users")

        val result = statement.resultSet
        if (result != null) {
            val columns = result.metaData.columnCount
            while (result.next()) {
                if (isUUID(result.getString(1)) && columns == 5)
                users.init(User(
                    UUID.fromString(result.getString(1)),
                    result.getString(2),
                    result.getString(3),
                    result.getBoolean(4),
                    result.getBoolean(5)
                ))
            }
        }
        statement.close()
    }

    @Throws(SQLException::class)
    fun initTable() {
        super.executeSqlStatement("INSERT INTO users (id, name, pass, isAdmin, isTeacher) " +
                "VALUES (" +
                "'4bed1b03-9f2d-44f9-b11a-fdd10bffd41e', " +
                "'Иванов И.И.', " +
                "'245a3577df1eab709644d367e4606ab602f4bf8338c14895880b78a7a448514e1308f77009aa0fc2ec513970eb1d124d4f9d197384bdb013ecf2b36afdf491a8', " +
                "false, " +
                "true" +
                ")")
        super.executeSqlStatement("INSERT INTO users (id, name, pass, isAdmin, isTeacher) " +
                "VALUES (" +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae', " +
                "'Петров П.П.', " +
                "'245a3577df1eab709644d367e4606ab602f4bf8338c14895880b78a7a448514e1308f77009aa0fc2ec513970eb1d124d4f9d197384bdb013ecf2b36afdf491a8', " +
                "false, " +
                "true" +
                ")")
        super.executeSqlStatement("INSERT INTO users (id, name, pass, isAdmin, isTeacher) " +
                "VALUES (" +
                "'00d1917a-e59e-411e-87c1-9b401e7e173c', " +
                "'Администратор', " +
                "'245a3577df1eab709644d367e4606ab602f4bf8338c14895880b78a7a448514e1308f77009aa0fc2ec513970eb1d124d4f9d197384bdb013ecf2b36afdf491a8', " +
                "true, " +
                "false" +
                ")")
    }

    @Throws(SQLException::class)
    fun createTable() {
        super.executeSqlStatement(
            "CREATE TABLE IF NOT EXISTS users " +
                    "( " +
                    "id VARCHAR(60) NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "pass VARCHAR(255) NOT NULL, " +
                    "isAdmin BOOLEAN NOT NULL DEFAULT false, " +
                    "isTeacher BOOLEAN NOT NULL DEFAULT true" +
                    ")"
        )
    }

    @Throws(SQLException::class)
    fun dropTable() {
        super.executeSqlStatement("DROP TABLE IF EXISTS users")
    }
}