package ru.ac.uniyar.domain.schedule

import ru.ac.uniyar.database.repository.SchedulesDB
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User
import java.sql.SQLException
import java.time.DayOfWeek
import java.util.*


class Schedules {
    private val schedules = mutableListOf<Schedule>()

    fun init(schedule: Schedule) {
        schedules.add(schedule)
    }

    fun add(schedule: Schedule, schedulesDB: SchedulesDB) {
        var newId = schedule.id
        while (schedules.any { it.id == schedule.id } || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        try {
            schedulesDB.addSchedule(Schedule(
                newId,
                schedule.group,
                schedule.dayOfWeek,
                schedule.classNumber,
                schedule.type,
                schedule.className,
                schedule.teacher,
                schedule.fractionClassName,
                schedule.fractionTeacher))
            schedules.add(Schedule(
                newId,
                schedule.group,
                schedule.dayOfWeek,
                schedule.classNumber,
                schedule.type,
                schedule.className,
                schedule.teacher,
                schedule.fractionClassName,
                schedule.fractionTeacher))
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }

    fun update(schedule: Schedule, schedulesDB: SchedulesDB) {
        try {
            schedulesDB.updateSchedule(schedule)
            schedules[schedules.indexOfFirst { it.id == schedule.id }] = schedule
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }
    fun updateTeacher(teacher: User) {
        while (schedules.any { it.teacher?.id == teacher.id && it.teacher.name != teacher.name }) {
            val index = schedules.indexOfFirst { it.teacher?.id == teacher.id }
            schedules[index] = Schedule(
                schedules[index].id,
                schedules[index].group,
                schedules[index].dayOfWeek,
                schedules[index].classNumber,
                schedules[index].type,
                schedules[index].className,
                teacher,
                schedules[index].fractionClassName,
                schedules[index].fractionTeacher)
        }
        while (schedules.any { it.fractionTeacher?.id == teacher.id && it.fractionTeacher.name != teacher.name }) {
            val index = schedules.indexOfFirst { it.fractionTeacher?.id == teacher.id }
            schedules[index] = Schedule(
                schedules[index].id,
                schedules[index].group,
                schedules[index].dayOfWeek,
                schedules[index].classNumber,
                schedules[index].type,
                schedules[index].className,
                schedules[index].teacher,
                schedules[index].fractionClassName,
                teacher)
        }
    }

    fun remove(schedule: Schedule?, schedulesDB: SchedulesDB) {
        if (schedule != null) {
            try {
                schedulesDB.deleteSchedule(schedule.id.toString())
                schedules.removeIf { it.id == schedule.id }
            } catch (e: SQLException) {
                e.printStackTrace()
                println("SQL Error!")
            }
        }
    }
    fun removeGroup(group: Group?, schedulesDB: SchedulesDB) {
        if (group != null) {
            try {
                schedulesDB.deleteGroupSchedule(group.id.toString())
                schedules.removeIf { it.group.id == group.id }
            } catch (e: SQLException) {
                e.printStackTrace()
                println("SQL Error!")
            }
        }
    }
    fun removeTeacher(teacher: User) {
        while (schedules.any { it.teacher?.id == teacher.id }) {
            val index = schedules.indexOfFirst { it.teacher?.id == teacher.id }
            schedules[index] = Schedule(
                schedules[index].id,
                schedules[index].group,
                schedules[index].dayOfWeek,
                schedules[index].classNumber,
                schedules[index].type,
                schedules[index].className,
                null,
                schedules[index].fractionClassName,
                schedules[index].fractionTeacher)
        }
        while (schedules.any { it.fractionTeacher?.id == teacher.id }) {
            val index = schedules.indexOfFirst { it.fractionTeacher?.id == teacher.id }
            schedules[index] = Schedule(
                schedules[index].id,
                schedules[index].group,
                schedules[index].dayOfWeek,
                schedules[index].classNumber,
                schedules[index].type,
                schedules[index].className,
                schedules[index].teacher,
                schedules[index].fractionClassName,
                null)
        }
    }

    fun filterGroupMonday(groupId: UUID): List<Schedule> {
        return schedules.filter { it.group.id == groupId && it.dayOfWeek == DayOfWeek.MONDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupTuesday(groupId: UUID): List<Schedule> {
        return schedules.filter { it.group.id == groupId && it.dayOfWeek == DayOfWeek.TUESDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupWednesday(groupId: UUID): List<Schedule> {
        return schedules.filter { it.group.id == groupId && it.dayOfWeek == DayOfWeek.WEDNESDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupThursday(groupId: UUID): List<Schedule> {
        return schedules.filter { it.group.id == groupId && it.dayOfWeek == DayOfWeek.THURSDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupFriday(groupId: UUID): List<Schedule> {
        return schedules.filter { it.group.id == groupId && it.dayOfWeek == DayOfWeek.FRIDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupSaturday(groupId: UUID): List<Schedule> {
        return schedules.filter { it.group.id == groupId && it.dayOfWeek == DayOfWeek.SATURDAY }.sortedBy { it.classNumber }
    }

    fun filterTeacherSchedule(teacher: User): List<Schedule> {
        return schedules.filter { it.teacher == teacher || it.fractionTeacher == teacher }.sortedBy { it.classNumber }
    }

    fun findLastClassNumber(group: Group?, dayOfWeek: DayOfWeek): Int {
        var maxNumber = 0
        for (i in 0 until fetchAll().count()) {
            if (schedules[i].group == group && schedules[i].dayOfWeek == dayOfWeek) {
                if (maxNumber < schedules[i].classNumber)
                    maxNumber = schedules[i].classNumber
            }
        }
        return maxNumber
    }

    fun hasClassNumber(group: Group?, dayOfWeek: DayOfWeek, classNumber: Int): Boolean {
        if (schedules.find { it.group == group && it.dayOfWeek == dayOfWeek && it.classNumber == classNumber } != null)
            return true
        return false
    }

    fun fetchString(uuid: String?): Schedule? {
        return try {
            schedules.find { it.id == UUID.fromString(uuid) }
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }

    fun fetchAll() : List<Schedule> = schedules
}