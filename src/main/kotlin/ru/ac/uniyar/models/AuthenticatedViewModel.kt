package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.user.User

sealed class AuthenticatedViewModel(val currentUser: User?) : ViewModel
