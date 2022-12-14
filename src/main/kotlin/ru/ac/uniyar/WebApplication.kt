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
import ru.ac.uniyar.computations.FetchUserViaToken
import ru.ac.uniyar.database.repository.GroupsDB
import ru.ac.uniyar.database.repository.SchedulesDB
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.InitData
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.filters.JwtTools
import ru.ac.uniyar.filters.authenticationFilter
import ru.ac.uniyar.filters.showErrorMessageFilter
import ru.ac.uniyar.handlers.*
import ru.ac.uniyar.routes.*
import ru.ac.uniyar.store.Settings
import ru.ac.uniyar.store.SettingsFileError
import kotlin.io.path.Path

fun app(
    users: Users,
    schedules: Schedules,
    groups: Groups,
    usersDB: UsersDB,
    groupsDB: GroupsDB,
    schedulesDB: SchedulesDB,
    currentUserLens: BiDiLens<Request, User?>,
    authenticateUserViaLoginQuery: AuthenticateUserViaLoginQuery,
    settings: Settings,
    html: BiDiBodyLens<ViewModel>,
    jwtTools: JwtTools
): HttpHandler = routes(
    static(ResourceLoader.Classpath("/ru/ac/uniyar/public/")),

    "/" bind GET to RedirectToMainHandler(),
    "/main" bind GET to ShowMainHandler(currentUserLens, html),

    "/schedule" bind GET to ShowScheduleHandler(currentUserLens, schedules, groups, html),
    "/schedule/edit/{id}" bind scheduleEditRoute(currentUserLens, users, schedules, schedulesDB, html),
    "/schedule/add" bind scheduleAddRoute(currentUserLens, users, schedules, groups, schedulesDB, html),
    "/schedule/remove/{id}" bind GET to ScheduleRemoveHandler(currentUserLens, schedules, schedulesDB),

    "/groups" bind GET to ShowGroupsHandler(currentUserLens, groups, html),
    "/group/add" bind groupAddEditRoute(currentUserLens, groups, groupsDB, html),
    "/group/edit" bind groupAddEditRoute(currentUserLens, groups, groupsDB, html),
    "/group/remove/{id}" bind GET to GroupRemoveHandler(currentUserLens, schedules, groups, groupsDB),

    "/login" bind GET to ShowLoginFormHandler(currentUserLens, users, html),
    "/login" bind Method.POST to AuthenticateUser(currentUserLens, authenticateUserViaLoginQuery, users, html, jwtTools),
    "/logout" bind GET to LogOutUser(),

    "/user" bind GET to ShowUserHandler(currentUserLens, schedules, html),
    "/users" bind GET to ShowUsersHandler(currentUserLens, users, html),
    "/user/add" bind userAddRoute(currentUserLens, users, usersDB, settings, html),
    "/user/edit/{id}" bind userEditRoute(currentUserLens, users, schedules, usersDB, html),
    "/user/remove/{id}" bind GET to UserRemoveHandler(currentUserLens, users, schedules, usersDB),
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

    val usersDB = UsersDB()
    val groupsDB = GroupsDB()
    val schedulesDB = SchedulesDB()

    InitData().initData(users, schedules, groups, usersDB, groupsDB, schedulesDB)

    /** Пароль от всех аккаунтов - 123 **/

    val contexts = RequestContexts()
    val currentUserLens = RequestContextKey.optional<User>(contexts)

    val authenticateUserViaLoginQuery = AuthenticateUserViaLoginQuery(users, settings)
    val fetchUserViaToken = FetchUserViaToken(users)

    val jwtTools = JwtTools(settings.salt, "ru.ac.uniyar.WebApplication")

    val renderer = PebbleTemplates().HotReload("src/main/resources")
    val htmlView = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()
    val printingApp: HttpHandler =
        ServerFilters.InitialiseRequestContext(contexts)
            .then(showErrorMessageFilter(currentUserLens, renderer))
            .then(authenticationFilter(currentUserLens, fetchUserViaToken, jwtTools))
            .then(app(users, schedules, groups, usersDB, groupsDB, schedulesDB, currentUserLens, authenticateUserViaLoginQuery, settings, htmlView, jwtTools))
    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on http://localhost:" + server.port())
}