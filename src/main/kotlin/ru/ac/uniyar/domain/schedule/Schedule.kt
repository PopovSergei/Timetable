package ru.ac.uniyar.domain.schedule

import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.teacher.Teacher
import java.time.DayOfWeek
import java.util.*

data class Schedule(
    val id: UUID,
    val group: Group,
    val dayOfWeek: DayOfWeek,
    val classNumber: Int,
    var className: String,
    var teacher: Teacher
)
