package ru.ac.uniyar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import ru.ac.uniyar.database.repository.GroupsDB
import ru.ac.uniyar.database.repository.SchedulesDB
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import java.time.DayOfWeek
import java.util.*

class DatabaseTest {
    private val usersDB = UsersDB()
    private val groupsDB = GroupsDB()
    private val schedulesDB = SchedulesDB()

    private val schedules = Schedules()
    private val groups = Groups()
    private val users = Users()

    private var group = Group(UUID.fromString("0ee6c6db-8b5f-4e50-bfc4-48b9b90723cb"), "TestName")
    private var user = User(
        UUID.fromString("5bee1b03-9f2d-44f9-b15a-fdd10bfad41e"), "Иванов И.И.",
        "245a3577df1eab709644d367e4606ab602f4bf8338c14895880b78a7a448514e1308f77009aa0fc2ec513970eb1d124d4f9d197384bdb013ecf2b36afdf491a8",
        false, true)
    private var schedule = Schedule(UUID.fromString("e75d570e-e9b2-459e-9323-7a3c155658e5"),
        group, DayOfWeek.MONDAY, 1, "static", "", null, "", null)


    @Test
    fun `Group add`() {
        groups.add(group, groupsDB)

        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertEquals(group, selectedGroup)
        groups.remove(schedules, group, groupsDB)
    }
    @Test
    fun `Group update`() {
        groups.add(group, groupsDB)
        group = Group(group.id, "NewTestName")
        groups.update(group, groupsDB)

        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertEquals(group, selectedGroup)
        groups.remove(schedules, group, groupsDB)
    }
    @Test
    fun `Group remove`() {
        groups.add(group, groupsDB)
        groups.remove(schedules, group, groupsDB)

        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertNull(selectedGroup)
    }

    @Test
    fun `User add`() {
        users.add(user, usersDB)

        val selectedUser = usersDB.selectUser(user.id.toString())
        assertEquals(user, selectedUser)
        users.remove(user, usersDB)
    }
    @Test
    fun `User update`() {
        users.add(user, usersDB)
        user = User(user.id, "Петров П.П.", user.pass, user.isAdmin, user.isTeacher)
        users.update(user, usersDB)

        val selectedUser = usersDB.selectUser(user.id.toString())
        assertEquals(user, selectedUser)
        users.remove(user, usersDB)
    }
    @Test
    fun `User remove`() {
        users.add(user, usersDB)
        users.remove(user, usersDB)

        val selectedUser = usersDB.selectUser(user.id.toString())
        assertNull(selectedUser)
    }

    @Test
    fun `Schedule add`() {
        groups.add(group, groupsDB)
        schedules.add(schedule, schedulesDB)

        val selectedSchedule = schedulesDB.selectSchedule(schedule.id.toString(), users, groups)
        assertEquals(schedule, selectedSchedule)
        schedules.remove(schedule, schedulesDB)
        groups.remove(schedules, group, groupsDB)
    }
    @Test
    fun `Schedule update`() {
        groups.add(group, groupsDB)
        schedules.add(schedule, schedulesDB)
        schedule = Schedule(UUID.fromString("e75d570e-e9b2-459e-9323-7a3c155658e5"),
            group, DayOfWeek.MONDAY, 1, "fraction", "Базы данных", null, "", null)
        schedules.update(schedule, schedulesDB)

        val selectedSchedule = schedulesDB.selectSchedule(schedule.id.toString(), users, groups)
        assertEquals(schedule, selectedSchedule)
        schedules.remove(schedule, schedulesDB)
        groups.remove(schedules, group, groupsDB)
    }
    @Test
    fun `Schedule remove`() {
        groups.add(group, groupsDB)
        schedules.add(schedule, schedulesDB)
        schedules.remove(schedule, schedulesDB)

        val selectedSchedule = schedulesDB.selectSchedule(schedule.id.toString(), users, groups)
        assertNull(selectedSchedule)
        groups.remove(schedules, group, groupsDB)
    }

    @Test
    fun `Group schedule remove`() {
        groups.add(group, groupsDB)
        schedules.add(schedule, schedulesDB)
        groups.remove(schedules, group, groupsDB)

        val selectedSchedule = schedulesDB.selectSchedule(schedule.id.toString(), users, groups)
        assertNull(selectedSchedule)
        val selectedGroup = groupsDB.selectGroup(group.id.toString())
        assertNull(selectedGroup)
    }

    @Test
    fun remove() {
        schedules.remove(schedule, schedulesDB)
        groups.remove(schedules, group, groupsDB)
        users.remove(user, usersDB)
    }
}