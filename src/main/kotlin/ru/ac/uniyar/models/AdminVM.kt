package ru.ac.uniyar.models

import ru.ac.uniyar.domain.user.User

class AdminVM(currentUser: User?) : AuthenticatedViewModel(currentUser)