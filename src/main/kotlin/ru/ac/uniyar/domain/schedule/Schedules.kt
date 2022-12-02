package ru.ac.uniyar.domain.schedule

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User
import java.time.DayOfWeek
import java.util.*


class Schedules {
    private val schedules = mutableListOf<Schedule>()

    fun add(schedule: Schedule) {
        var newId = schedule.id
        while (schedules.any { it.id == schedule.id } || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
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
    }

    fun update(schedule: Schedule) {
        schedules[schedules.indexOfFirst { it.id == schedule.id }] = schedule
    }

    fun remove(schedule: Schedule?) {
        if (schedule != null) {
            schedules.removeIf { it == schedule }
        }
    }
    fun removeGroup(group: Group?) {
        if (group != null) {
            schedules.removeIf { it.group == group }
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