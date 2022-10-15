package ru.ac.uniyar.domain.admin

import java.util.UUID

data class Admin(
    val id: UUID,
    val name: String,
    val pass : String
)