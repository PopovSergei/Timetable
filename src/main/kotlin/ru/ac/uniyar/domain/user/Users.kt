package ru.ac.uniyar.domain.user

import ru.ac.uniyar.domain.EMPTY_UUID
import java.util.*

class Users {
    private val users = mutableListOf<User>()

    fun add(user: User) {
        var newId = user.id
        while (users.any { it.id == user.id } || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        users.add(User(newId, user.name, user.pass, user.isAdmin, user.isTeacher))
    }

    fun update(user: User) {
        users[users.indexOfFirst { it.id == user.id }] = user
    }

    fun remove(user: User?) {
        if (user != null) {
            users.removeIf { it == user }
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