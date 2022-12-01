package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User

class ShowGroupAddEditFVM(currentUser: User?, val group: Group?, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)