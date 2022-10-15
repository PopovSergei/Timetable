package ru.ac.uniyar.computations

import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.teacher.Teachers
import java.util.*

class FetchAccessViaToken(
    private val teachers: Teachers,
    private val admins: Admins
) {
    operator fun invoke(token: String): String? {
        val uuid = try {
            UUID.fromString(token)
        } catch (_: IllegalArgumentException) {
            return null
        }
        if (admins.fetch(uuid) != null) {
            return "admin"
        }
        if (teachers.fetch(uuid) != null) {
            return "teacher"
        }
        return "anon"
    }
}