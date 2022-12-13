package ru.ac.uniyar.database.repository

import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.util.UUID


class GroupsDB: BaseTable() {
    @Throws(SQLException::class)
    fun users() {}

    @Throws(SQLException::class)
    fun selectGroup(groupId: String): Group? {
        var group: Group? = null

        reopenConnection()
        val sqlStatement = "SELECT * FROM groups WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, groupId)
        statement.execute()

        val result = statement.resultSet
        if (result != null) {
            val columns = result.metaData.columnCount
            while (result.next()) {
                if (isUUID(result.getString(1)) && columns == 2)
                    group = Group(
                        UUID.fromString(result.getString(1)),
                        result.getString(2))
            }
        }
        statement.close()
        return group
    }

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
    fun addGroup(id: String, name: String) {
        reopenConnection()
        val sqlStatement = "INSERT INTO groups (id, name) VALUES (?,?)"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, id)
        statement.setString(2, name)
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun updateGroup(group: Group) {
        reopenConnection()
        val sqlStatement = "UPDATE groups SET name = ? WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, group.name)
        statement.setString(2, group.id.toString())
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun deleteGroup(groupId: String) {
        reopenConnection()
        val sqlStatement = "DELETE FROM groups WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, groupId)
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun initGroups(groups: Groups) {
        reopenConnection()
        val statement: Statement = connection!!.createStatement()
        statement.execute("SELECT * FROM groups")

        val result = statement.resultSet
        if (result != null) {
            val columns = result.metaData.columnCount
            while (result.next()) {
                if (isUUID(result.getString(1)) && columns == 2)
                    groups.init(Group(
                        UUID.fromString(result.getString(1)),
                        result.getString(2)
                    ))
            }
        }
        statement.close()
    }

    @Throws(SQLException::class)
    fun initTable() {
        super.executeSqlStatement("INSERT INTO groups (id, name) " +
                "VALUES (" +
                "'7c049cfc-e178-49ff-995e-ebc4fe20c446', " +
                "'ИВТ-1'" +
                ")")
        super.executeSqlStatement("INSERT INTO groups (id, name) " +
                "VALUES (" +
                "'97db0393-5c16-4280-a861-8514db7d6a87', " +
                "'ИВТ-2'" +
                ")")
        super.executeSqlStatement("INSERT INTO groups (id, name) " +
                "VALUES (" +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'ИВТ-3'" +
                ")")
        super.executeSqlStatement("INSERT INTO groups (id, name) " +
                "VALUES (" +
                "'a558472e-4fef-4df5-9da4-08fb387984b1', " +
                "'ИВТ-4'" +
                ")")
        super.executeSqlStatement("INSERT INTO groups (id, name) " +
                "VALUES (" +
                "'a9087dfc-44e5-4d2d-bdab-a576c1a1e098', " +
                "'ИТ-1'" +
                ")")
        super.executeSqlStatement("INSERT INTO groups (id, name) " +
                "VALUES (" +
                "'ceaed28d-609d-49cc-b61d-baaaaf768c60', " +
                "'ПИЭ-1'" +
                ")")
    }

    @Throws(SQLException::class)
    fun createTable() {
        super.executeSqlStatement(
            "CREATE TABLE IF NOT EXISTS groups " +
                    "( " +
                    "id VARCHAR(64) NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL " +
                    ")"
        )
    }

    @Throws(SQLException::class)
    fun dropTable() {
        super.executeSqlStatement("DROP TABLE IF EXISTS groups")
    }
}