package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User

class ShowScheduleAddFVM(
    currentUser: User?,
    val teachers: List<User>,
    val form: WebForm = WebForm()
): AuthenticatedViewModel(currentUser)