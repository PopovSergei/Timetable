package ru.ac.uniyar.domain

import ru.ac.uniyar.computations.hashPassword
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.store.Settings
import java.time.DayOfWeek
import java.util.*

class InitSome {
    fun initSome(
        users: Users,
        schedules: Schedules,
        groups: Groups,
        settings: Settings,
    ) {
        val user1 = User(UUID.randomUUID(), "Полетаев А.Ю.", hashPassword("123", settings.salt), false, true)
        val user2 = User(UUID.randomUUID(), "Богомолов Ю.В.", hashPassword("123", settings.salt), false, true)
        val user3 = User(UUID.randomUUID(), "Деканат", hashPassword("123", settings.salt), true, false)

        users.add(user1)
        users.add(user2)
        users.add(user3)

        groups.add(Group(UUID.randomUUID(), "ИВТ-1"))
        groups.add(Group(UUID.randomUUID(), "ИВТ-2"))
        groups.add(Group(UUID.randomUUID(), "ИВТ-3"))
        groups.add(Group(UUID.randomUUID(), "ИВТ-4"))
        groups.add(Group(UUID.randomUUID(), "ИТ-1"))
        groups.add(Group(UUID.randomUUID(), "ПИЭ-1"))

        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.MONDAY, 1, "static", "Математика, 220", user2, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.MONDAY, 3, "static", "Базы данных, 210", user1, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.MONDAY, 2, "static", "Информатика, 216", user1, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(1)!!, DayOfWeek.MONDAY, 1, "static", "Математика, 215", user2, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.TUESDAY, 1, "static", "Математика, 220", user2, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.THURSDAY, 1, "static", "Информатика, 210", user1, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.WEDNESDAY, 1, "static", "Математика, 219", user2, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.FRIDAY, 1, "static", "Информатика, 216", user1, "", null))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.SATURDAY, 1, "static", "Математика, 220", user2, "", null))
    }
}