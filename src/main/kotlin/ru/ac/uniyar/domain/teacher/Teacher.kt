package ru.ac.uniyar.domain.teacher

import java.util.UUID

data class Teacher(
    val id: UUID,
    val name: String,
    val pass : String
)