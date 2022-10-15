package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.user.User

class ShowLoginFVM(
    currentUser: User?,
    val users: Iterable<IndexedValue<User>>,
    val user: User?,
    val form: WebForm = WebForm()
) : AuthenticatedViewModel(currentUser)