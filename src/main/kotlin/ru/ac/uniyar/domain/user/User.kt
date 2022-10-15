package ru.ac.uniyar.domain.user

import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
    val pass : String
)