package ru.ac.uniyar.domain.user

import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.EMPTY_UUID
import java.sql.SQLException
import java.util.*

class Users {
    private val users = mutableListOf<User>()

    fun init(user: User) {
        users.add(user)
    }

    fun add(user: User, usersDB: UsersDB) {
        var newId = user.id
        while (users.any { it.id == user.id } || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        try {
            usersDB.addUser(newId.toString(), user.name, user.pass, user.isAdmin, user.isTeacher)
            users.add(User(newId, user.name, user.pass, user.isAdmin, user.isTeacher))
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }

    fun update(user: User, usersDB: UsersDB) {
        try {
            usersDB.updateUser(user)
            users[users.indexOfFirst { it.id == user.id }] = user
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }

    fun remove(user: User?, usersDB: UsersDB) {
        if (user != null) {
            try {
                usersDB.deleteUser(user.id.toString())
                users.removeIf { it.id == user.id }
            } catch (e: SQLException) {
                e.printStackTrace()
                println("SQL Error!")
            }
        }
    }

    fun fetch(uuid: UUID): User? {
        return users.find { it.id == uuid }
    }
    fun fetchString(uuid: String?): User? {
        return try {
            users.find { it.id == UUID.fromString(uuid) }
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }
    fun fetchTeachers() : List<User> {
        return users.filter { it.isTeacher }.sortedBy { it.name }
    }
    fun fetchAll() : List<User> = users.sortedBy { it.name }
}