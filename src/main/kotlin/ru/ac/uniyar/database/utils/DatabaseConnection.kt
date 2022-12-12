package ru.ac.uniyar.database.utils

import java.sql.*


class DatabaseConnection {
    private val url = "jdbc:h2:./src/main/kotlin/ru/ac/uniyar/database/db/stockExchange"
    private val driver = "org.h2.Driver"

    @Throws(SQLException::class)
    fun createConnection(): Connection? {
        try {
            Class.forName(driver) //Проверяем наличие JDBC драйвера для работы с БД
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            println("JDBC driver not found!")
        }
        return DriverManager.getConnection(url)
    }
}