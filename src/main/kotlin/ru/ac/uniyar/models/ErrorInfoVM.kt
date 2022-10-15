package ru.ac.uniyar.models

import org.http4k.core.Uri
import ru.ac.uniyar.domain.user.User

class ErrorInfoVM(currentUser: User?, val uri: Uri): AuthenticatedViewModel(currentUser)