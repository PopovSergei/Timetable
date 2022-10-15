package ru.ac.uniyar.domain.group

import ru.ac.uniyar.domain.EMPTY_UUID
import java.util.*

class Groups {
    private val groups = mutableListOf<Group>()

    fun toSting() : String {
        return groups.joinToString("\n") { rec -> rec.name }
    }

    private fun toUuid() : String {
        return groups.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(group: Group) {
        var newId = group.id
        while (toUuid().contains(group.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        groups.add(Group(newId, group.name))
    }

    fun removeGroup(id: String) {
        groups.remove(groups.find { it.id.toString() == id })
    }

    fun fetchOne(index: Int): Group? {
        return groups.getOrNull(index)
    }

    fun fetch(uuid: UUID): Group? {
        return groups.find { it.id == uuid }
    }

    fun fetchAll() : List<Group> = groups.sortedBy { it.name }
}