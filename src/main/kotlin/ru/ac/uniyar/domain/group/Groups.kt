package ru.ac.uniyar.domain.group

import ru.ac.uniyar.domain.EMPTY_UUID
import java.util.*

class Groups {
    private val groups = mutableListOf<Group>()

    fun add(group: Group) {
        var newId = group.id
        while (groups.any { it.id == group.id } || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        groups.add(Group(newId, group.name))
    }

    fun update(group: Group) {
        groups[groups.indexOfFirst { it.id == group.id }] = group
    }

    fun remove(group: Group?) {
        if (group != null) {
            groups.removeIf { it == group }
        }
    }

    fun hasSameName(groupName: String?): Boolean {
        if (groups.find { it.name == groupName } != null)
            return true
        return false
    }

    fun fetchOne(index: Int): Group? {
        return groups.getOrNull(index)
    }
    fun fetchString(uuid: String?): Group? {
        return try {
            groups.find { it.id == UUID.fromString(uuid) }
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }
    fun fetchAll() : List<Group> = groups.sortedBy { it.name }
}