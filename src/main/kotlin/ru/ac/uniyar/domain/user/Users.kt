package ru.ac.uniyar.domain.user

import ru.ac.uniyar.domain.EMPTY_UUID
import java.util.*

class Users {
    private val users = mutableListOf<User>()

    fun toSting() : String {
        return users.joinToString("\n") { rec -> rec.name }
    }

    private fun toUuid() : String {
        return users.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(user: User) {
        var newId = user.id
        while (toUuid().contains(user.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        users.add(User(newId, user.name, user.pass, user.isAdmin, user.isTeacher))
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
        return users.filter { it.isTeacher }
    }

    fun fetchAll() : List<User> = users
}