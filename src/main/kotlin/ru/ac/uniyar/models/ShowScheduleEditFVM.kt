package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.user.User

class ShowScheduleEditFVM(currentUser: User?, val scheduleToEdit: Schedule?, val teachers: List<User>, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)