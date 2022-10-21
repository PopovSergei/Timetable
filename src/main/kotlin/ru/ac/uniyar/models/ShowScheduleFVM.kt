package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.teacher.Teacher
import ru.ac.uniyar.domain.user.User

class ShowScheduleFVM(currentUser: User?, val scheduleToEdit: Schedule?, val teachers: Iterable<IndexedValue<Teacher>>, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)