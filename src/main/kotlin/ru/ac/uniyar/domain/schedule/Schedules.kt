package ru.ac.uniyar.domain.schedule

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User
import java.time.DayOfWeek
import java.util.*


class Schedules {
    private val schedules = mutableListOf<Schedule>()

    private fun toUuid() : String {
        return schedules.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(schedule: Schedule) {
        var newId = schedule.id
        while (toUuid().contains(schedule.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        schedules.add(Schedule(newId, schedule.group, schedule.dayOfWeek, schedule.classNumber, schedule.className, schedule.teacher))
    }

    fun update(schedule: Schedule, newClassName: String, newTeacher: User?) {
        for(i in 0 until fetchAll().count()) {
            if (schedules[i].id == schedule.id) {
                if (newTeacher != null)
                schedules[i] = Schedule(
                    schedule.id,
                    schedule.group,
                    schedule.dayOfWeek,
                    schedule.classNumber,
                    newClassName,
                    newTeacher)
                else
                    schedules[i] = Schedule(
                        schedule.id,
                        schedule.group,
                        schedule.dayOfWeek,
                        schedule.classNumber,
                        newClassName,
                        null)
            }
        }
    }

    fun remove(group: Group?, dayOfWeek: DayOfWeek) {
        val lastClass = findLastClassNumber(group, dayOfWeek)
        if (lastClass > 0) {
            schedules.removeIf { it.group == group && it.dayOfWeek == dayOfWeek && it.classNumber == lastClass }
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

    fun fetchString(uuid: String?): Schedule? {
        return try {
            schedules.find { it.id == UUID.fromString(uuid) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun fetch(uuid: UUID): Schedule? {
        return schedules.find { it.id == uuid }
    }

    fun fetchAll() : List<Schedule> = schedules
}