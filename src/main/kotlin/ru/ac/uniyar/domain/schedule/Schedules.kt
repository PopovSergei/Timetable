package ru.ac.uniyar.domain.schedule

import ru.ac.uniyar.domain.EMPTY_UUID
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

    fun filterGroupMonday(group: String): List<Schedule> {
        return schedules.filter { it.group.name == group && it.dayOfWeek == DayOfWeek.MONDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupTuesday(group: String): List<Schedule> {
        return schedules.filter { it.group.name == group && it.dayOfWeek == DayOfWeek.TUESDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupWednesday(group: String): List<Schedule> {
        return schedules.filter { it.group.name == group && it.dayOfWeek == DayOfWeek.WEDNESDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupThursday(group: String): List<Schedule> {
        return schedules.filter { it.group.name == group && it.dayOfWeek == DayOfWeek.THURSDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupFriday(group: String): List<Schedule> {
        return schedules.filter { it.group.name == group && it.dayOfWeek == DayOfWeek.FRIDAY }.sortedBy { it.classNumber }
    }
    fun filterGroupSaturday(group: String): List<Schedule> {
        return schedules.filter { it.group.name == group && it.dayOfWeek == DayOfWeek.SATURDAY }.sortedBy { it.classNumber }
    }

    fun fetch(uuid: UUID): Schedule? {
        return schedules.find { it.id == uuid }
    }

    fun fetchAll() : Iterable<IndexedValue<Schedule>> = schedules.withIndex()
}