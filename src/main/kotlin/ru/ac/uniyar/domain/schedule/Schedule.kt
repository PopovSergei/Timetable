package ru.ac.uniyar.domain.schedule

import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User
import java.time.DayOfWeek
import java.util.*

data class Schedule(
    val id: UUID,
    val group: Group,
    val dayOfWeek: DayOfWeek,
    val classNumber: Int,
    val type: String,
    val className: String,
    val teacher: User?,
    val fractionClassName: String,
    val fractionTeacher: User?
)
