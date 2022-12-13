package ru.ac.uniyar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.ac.uniyar.database.repository.GroupsDB
import ru.ac.uniyar.database.repository.SchedulesDB
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.group.Group
import java.util.*

class DatabaseTest {
    private val groupsDB = GroupsDB()
    private var group = Group(UUID.fromString("0ee6c6db-8b5f-4e50-bfc4-48b9b90723cb"), "TestName")
    @Test
    fun `Group add test`() {
        groupsDB.addGroup(group.id.toString(), group.name)
        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertEquals(group,selectedGroup)
        groupsDB.deleteGroup(group.id.toString())
    }

    @Test
    fun `Group update test`() {
        groupsDB.addGroup(group.id.toString(), group.name)
        group = Group(group.id, "NewTestName")
        groupsDB.updateGroup(group)
        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertEquals(group,selectedGroup)
        groupsDB.deleteGroup(group.id.toString())
    }

    @Test
    fun `Group remove test`() {
        groupsDB.addGroup(group.id.toString(), group.name)
        groupsDB.deleteGroup(group.id.toString())
        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertEquals(null,selectedGroup)
    }
}