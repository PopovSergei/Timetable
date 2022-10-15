package ru.ac.uniyar.computations

import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import java.util.UUID

class FetchUserViaToken(
    private val users: Users
    ) {
    operator fun invoke(token: String): User? {
        val uuid = try {
            UUID.fromString(token)
        } catch (_: IllegalArgumentException) {
            return null
        }
        return users.fetch(uuid)
    }
}