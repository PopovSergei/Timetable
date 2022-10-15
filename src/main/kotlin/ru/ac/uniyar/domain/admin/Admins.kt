package ru.ac.uniyar.domain.admin

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.user.User
import java.util.*

class Admins {
    private val admins = mutableListOf<Admin>()

    fun toSting() : String {
        return admins.joinToString("\n") { rec -> rec.name }
    }

    private fun toUuid() : String {
        return admins.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(admin: Admin) {
        var newId = admin.id
        while (toUuid().contains(admin.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        admins.add(Admin(newId, admin.name, admin.pass))
    }

    fun addUser(admin: User) {
        var newId = admin.id
        while (toUuid().contains(admin.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        admins.add(Admin(newId, admin.name, admin.pass))
    }

    fun fetchOne(index: Int): Admin? {
        return admins.getOrNull(index)
    }

    fun fetch(uuid: UUID): Admin? {
        return admins.find { it.id == uuid }
    }

    fun fetchAll() : Iterable<IndexedValue<Admin>> = admins.withIndex()
}