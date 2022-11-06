package ru.ac.uniyar.models

import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.user.User

class ScheduleVM(
    currentUser: User?,
    val groups: List<Group>,
    val group: Group?,
    val monday: List<Schedule>,
    val tuesday: List<Schedule>,
    val wednesday: List<Schedule>,
    val thursday: List<Schedule>,
    val friday: List<Schedule>,
    val saturday: List<Schedule>
): AuthenticatedViewModel(currentUser)