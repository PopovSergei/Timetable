package ru.ac.uniyar.store

import org.http4k.format.Jackson
import java.nio.file.Path
import kotlin.io.path.isReadable

class Settings(settingsPath: Path) {
    val salt: String

    init {
        if (!settingsPath.isReadable()) throw SettingsFileError("Configuration file $settingsPath")

        val file = settingsPath.toFile()
        val jsonDocument = file.readText()
        val node = Jackson.parse(jsonDocument)

        if (!node.hasNonNull("salt")) throw SettingsFileError("Configuration file $settingsPath")

        salt = node["salt"].asText()
    }
}

class SettingsFileError(message: String) : RuntimeException(message)