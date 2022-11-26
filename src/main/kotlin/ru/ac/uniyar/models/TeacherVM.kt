package ru.ac.uniyar.models

import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.user.User

class TeacherVM(currentUser: User?, val teacherSchedule: List<Schedule>) : AuthenticatedViewModel(currentUser)