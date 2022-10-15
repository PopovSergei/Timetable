package ru.ac.uniyar.domain.teacher

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.admin.Admin
import ru.ac.uniyar.domain.user.User
import java.util.*

class Teachers {
    private val teachers = mutableListOf<Teacher>()

    fun toSting() : String {
        return teachers.joinToString("\n") { rec -> rec.name }
    }

    private fun toUuid() : String {
        return teachers.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(teacher: Teacher) {
        var newId = teacher.id
        while (toUuid().contains(teacher.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        teachers.add(Teacher(newId, teacher.name, teacher.pass))
    }

    fun addUser(teacher: User) {
        var newId = teacher.id
        while (toUuid().contains(teacher.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        teachers.add(Teacher(newId, teacher.name, teacher.pass))
    }

    fun removeTeacher(id: String) {
        teachers.remove(teachers.find { it.id.toString() == id })
    }

    fun fetchOne(index: Int): Teacher? {
        return teachers.getOrNull(index)
    }

    fun fetch(uuid: UUID): Teacher? {
        return teachers.find { it.id == uuid }
    }

    fun fetchAll() : Iterable<IndexedValue<Teacher>> = teachers.withIndex()
}