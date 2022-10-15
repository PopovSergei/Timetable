package ru.ac.uniyar.computations

import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.store.Settings
import java.util.*

class AuthenticateUserViaLoginQuery(
    private val users: Users,
    private val settings: Settings,
) {

    operator fun invoke(id: UUID, password: String): String {
        val user = users.fetch(id)
        val hashedPassword = hashPassword(password, settings.salt)
        if (user != null) {
            if (hashedPassword != user.pass)
                throw AuthenticationError()
        }
        return user!!.id.toString()
    }
}

class AuthenticationError : RuntimeException("User or password is wrong")