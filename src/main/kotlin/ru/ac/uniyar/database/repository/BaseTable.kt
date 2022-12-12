package ru.ac.uniyar.database.repository

import ru.ac.uniyar.database.utils.DatabaseConnection
import java.sql.*


open class BaseTable {
    var connection: Connection? = null

    @Throws(SQLException::class)
    fun baseTable() {
        connection = DatabaseConnection().createConnection()
    }

    @Throws(SQLException::class)
    fun executeSqlStatement(sqlStatement: String?) {
        reopenConnection()
        val statement: Statement = connection!!.createStatement()
        statement.execute(sqlStatement)
        statement.close()
    }

    @Throws(SQLException::class)
    fun reopenConnection() {
        if (connection == null || connection!!.isClosed) {
            connection = DatabaseConnection().createConnection()
        }
    }
    @Throws(SQLException::class)
    fun close() {
        try {
            if (connection != null && !connection!!.isClosed) {
                connection!!.close()
                println("Connection closed.")
            }
        } catch (e: SQLException) {
            println("Can't close SQL connection!")
        }
    }
}