package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.user.User

class ShowScheduleAddFVM(
    currentUser: User?,
    val lastClassNumber: Int,
    val teacher: User?,
    val teachers: List<User>,
    val form: WebForm = WebForm()
): AuthenticatedViewModel(currentUser)