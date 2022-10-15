package ru.ac.uniyar.computations

fun hashPassword(password: String, salt: String): String {
    val saltedPassword = password + salt
    return Checksum(saltedPassword.toByteArray()).toSting()
}