package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.models.ShowUserEditFVM

fun userEditRoute(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    usersDB: UsersDB,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showUserEditForm(currentUserLens, users, htmlView),
    "/" bind Method.POST to editUserWithLens(currentUserLens, users, schedules, usersDB, htmlView)
)

fun showUserEditForm(currentUserLens: BiDiLens<Request, User?>, users: Users, htmlView: BiDiBodyLens<ViewModel>): HttpHandler = { request->
    val currentUser = currentUserLens(request)
    val userId = request.path("id").orEmpty()

    if (currentUser?.isAdmin == true) {
        if (users.fetchString(userId) != null) {
            Response(Status.OK).with(htmlView of ShowUserEditFVM(currentUser, users.fetchString(userId)))
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val userNameFormLens = FormField.string().required("userName")
private val userFormLens = Body.webForm(
    Validator.Feedback,
    userNameFormLens
).toLens()

fun editUserWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    usersDB: UsersDB,
    htmlView: BiDiBodyLens<ViewModel>,
): HttpHandler = { request ->
    val webForm = userFormLens(request)
    val currentUser = currentUserLens(request)
    val userId = request.path("id").orEmpty()

    if (currentUser?.isAdmin == true) {
        if (users.fetchString(userId) != null) {
            if (webForm.errors.isEmpty()) {
                val user = users.fetchString(userId)
                val newUser = User(user!!.id, userNameFormLens(webForm), user.pass, user.isAdmin, user.isTeacher)
                users.update(newUser, usersDB)
                if (newUser.isTeacher) {
                    schedules.updateTeacher(newUser)
                }
                Response(Status.FOUND).header("Location", "/users")
            } else {
                Response(Status.OK).with(htmlView of ShowUserEditFVM(currentUser, users.fetchString(userId), webForm))
            }
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}