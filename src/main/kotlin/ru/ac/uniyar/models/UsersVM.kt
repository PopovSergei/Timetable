package ru.ac.uniyar.models

import ru.ac.uniyar.domain.user.User

class UsersVM(currentUser: User?, val users: List<User>) : AuthenticatedViewModel(currentUser)