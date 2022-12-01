package ru.ac.uniyar.models

import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.user.User

class GroupsVM(currentUser: User?, val groups: List<Group>) : AuthenticatedViewModel(currentUser)