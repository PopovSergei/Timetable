package ru.ac.uniyar

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.lens.RequestContextKey
import org.http4k.routing.*
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import ru.ac.uniyar.computations.AuthenticateUserViaLoginQuery
import ru.ac.uniyar.computations.FetchAccessViaToken
import ru.ac.uniyar.computations.FetchUserViaToken
import ru.ac.uniyar.domain.InitSome
import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.teacher.Teachers
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.filters.JwtTools
import ru.ac.uniyar.filters.accessFilter
import ru.ac.uniyar.filters.authenticationFilter
import ru.ac.uniyar.filters.showErrorMessageFilter
import ru.ac.uniyar.handlers.*
import ru.ac.uniyar.store.Settings
import ru.ac.uniyar.store.SettingsFileError
import kotlin.io.path.Path

fun app(
    users: Users,
    teachers: Teachers,
    admins: Admins,
    schedules: Schedules,
    groups: Groups,
    settings: Settings,
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    authenticateUserViaLoginQuery: AuthenticateUserViaLoginQuery,
    html: BiDiBodyLens<ViewModel>,
    jwtTools: JwtTools
): HttpHandler = routes(
    static(ResourceLoader.Classpath("/ru/ac/uniyar/public/")),

    "/" bind GET to RedirectToMainHandler(),
    "/main" bind GET to ShowMainHandler(currentUserLens, html),

    "/schedule" bind GET to ShowScheduleHandler(schedules, groups, currentUserLens, currentAccessLens, html),

    "/login" bind GET to ShowLoginFormHandler(currentUserLens, users, html),
    "/login" bind Method.POST to AuthenticateUser(currentUserLens, authenticateUserViaLoginQuery, users, html, jwtTools),
    "/logout" bind GET to LogOutUser(),
)

fun main() {
    val settings = try {
        Settings(Path("settings.json"))
    } catch (error: SettingsFileError) {
        println(error.message)
        return
    }

    val schedules = Schedules()
    val groups = Groups()
    val users = Users()
    val teachers = Teachers()
    val admins = Admins()
    InitSome().initSome(users, teachers, admins, schedules, groups, settings)

    /** Пароль от всех аккаунтов - 123 **/

    val contexts = RequestContexts()
    val currentUserLens = RequestContextKey.optional<User>(contexts)
    val currentAccessLens = RequestContextKey.optional<String>(contexts)

    val authenticateUserViaLoginQuery = AuthenticateUserViaLoginQuery(users, settings)
    val fetchUserViaToken = FetchUserViaToken(users)
    val fetchAccessViaToken = FetchAccessViaToken(teachers, admins)

    val jwtTools = JwtTools(settings.salt, "ru.ac.uniyar.WebApplication")

    val renderer = PebbleTemplates().HotReload("src/main/resources")
    val htmlView = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()
    val printingApp: HttpHandler =
        ServerFilters.InitialiseRequestContext(contexts)
            .then(showErrorMessageFilter(currentUserLens, renderer))
            .then(authenticationFilter(currentUserLens, fetchUserViaToken, jwtTools))
            .then(accessFilter(currentAccessLens, fetchAccessViaToken, jwtTools))
            .then(app(users, teachers, admins, schedules, groups, settings, currentUserLens, currentAccessLens, authenticateUserViaLoginQuery, htmlView, jwtTools))
    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on http://localhost:" + server.port())
}