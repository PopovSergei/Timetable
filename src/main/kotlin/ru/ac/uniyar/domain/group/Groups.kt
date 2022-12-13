package ru.ac.uniyar.domain.group

import ru.ac.uniyar.database.repository.GroupsDB
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.user.User
import java.sql.SQLException
import java.util.*

class Groups {
    private val groups = mutableListOf<Group>()

    fun init(group: Group) {
        groups.add(group)
    }

    fun add(group: Group, groupsDB: GroupsDB) {
        var newId = group.id
        while (groups.any { it.id == group.id } || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        try {
            groupsDB.addGroup(newId.toString(), group.name)
            groups.add(Group(newId, group.name))
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }

    fun update(group: Group, groupsDB: GroupsDB) {
        try {
            groupsDB.updateGroup(group)
            groups[groups.indexOfFirst { it.id == group.id }] = group
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }

    fun remove(group: Group?, groupsDB: GroupsDB) {
        if (group != null) {
            try {
                groupsDB.deleteGroup(group.id.toString())
                groups.removeIf { it.id == group.id }
            } catch (e: SQLException) {
                e.printStackTrace()
                println("SQL Error!")
            }
        }
    }

    fun hasSameName(groupName: String?): Boolean {
        if (groups.find { it.name == groupName } != null)
            return true
        return false
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