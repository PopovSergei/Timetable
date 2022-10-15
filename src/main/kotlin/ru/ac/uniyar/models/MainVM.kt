package ru.ac.uniyar.models

import ru.ac.uniyar.domain.user.User

class MainVM(currentUser: User?) : AuthenticatedViewModel(currentUser)
