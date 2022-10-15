package ru.ac.uniyar.domain

import ru.ac.uniyar.computations.hashPassword
import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.teacher.Teachers
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.store.Settings
import java.time.DayOfWeek
import java.util.*

class InitSome {
    fun initSome(
        users: Users,
        teachers: Teachers,
        admins: Admins,
        schedules: Schedules,
        groups: Groups,
        settings: Settings,
    ) {
        val user1 = User(UUID.randomUUID(), "Петя", hashPassword("123", settings.salt))
        val user2 = User(UUID.randomUUID(), "Вася", hashPassword("123", settings.salt))
        val user3 = User(UUID.randomUUID(), "Админ", hashPassword("123", settings.salt))
        users.add(user1)
        users.add(user2)
        users.add(user3)
        teachers.addUser(user1)
        teachers.addUser(user2)
        admins.addUser(user3)
        groups.add(Group(UUID.randomUUID(), "ИВТ-1"))
        groups.add(Group(UUID.randomUUID(), "ИВТ-2"))
        groups.add(Group(UUID.randomUUID(), "ИВТ-3"))
        groups.add(Group(UUID.randomUUID(), "ИВТ-4"))
        groups.add(Group(UUID.randomUUID(), "ИТ-1"))
        groups.add(Group(UUID.randomUUID(), "ПИЭ-1"))

        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.MONDAY, 1, "Математика", teachers.fetchOne(1)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.MONDAY, 3, "Базы данных", teachers.fetchOne(0)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.MONDAY, 2, "Информатика", teachers.fetchOne(1)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(1)!!, DayOfWeek.MONDAY, 1, "Математика", teachers.fetchOne(1)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.TUESDAY, 1, "Математика", teachers.fetchOne(0)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.THURSDAY, 1, "Информатика", teachers.fetchOne(0)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.WEDNESDAY, 1, "Математика", teachers.fetchOne(1)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.FRIDAY, 1, "Информатика", teachers.fetchOne(0)!!))
        schedules.add(Schedule(EMPTY_UUID, groups.fetchOne(2)!!, DayOfWeek.SATURDAY, 1, "Математика", teachers.fetchOne(1)!!))
    }
}