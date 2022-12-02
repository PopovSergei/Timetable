package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.computations.hashPassword
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.handlers.lensOrNull
import ru.ac.uniyar.models.ShowUserAddFVM
import ru.ac.uniyar.store.Settings
import java.util.*

fun userAddRoute(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    settings: Settings,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showUserAddForm(currentUserLens, htmlView),
    "/" bind Method.POST to addUserWithLens(currentUserLens, users, settings, htmlView)
)

fun showUserAddForm(currentUserLens: BiDiLens<Request, User?>, htmlView: BiDiBodyLens<ViewModel>): HttpHandler = { request->
    val currentUser = currentUserLens(request)
    if (currentUser?.isAdmin == true) {
        Response(Status.OK).with(htmlView of ShowUserAddFVM(currentUser))
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val userTypeFormLens = FormField.nonEmptyString().required("userType")
private val userNameFormLens = FormField.nonEmptyString().required("userName")
private val firstPassFormLens = FormField.nonEmptyString().required("firstPass")
private val secondPassFormLens = FormField.nonEmptyString().required("secondPass")
private val userFormLens = Body.webForm(
    Validator.Feedback,
    userTypeFormLens,
    userNameFormLens,
    firstPassFormLens,
    secondPassFormLens,
).toLens()

fun addUserWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    settings: Settings,
    htmlView: BiDiBodyLens<ViewModel>,
): HttpHandler = { request ->
    var webForm = userFormLens(request)
    val currentUser = currentUserLens(request)
    val firstPass = lensOrNull(firstPassFormLens, webForm)
    val secondPass = lensOrNull(secondPassFormLens, webForm)

    if (firstPass != null && firstPass != secondPass) {
        val newErrors = webForm.errors + Invalid(firstPassFormLens.meta.copy(description = "Passwords should match"))
        webForm = webForm.copy(errors = newErrors)
    }

    if (currentUser?.isAdmin == true) {
        if (webForm.errors.isEmpty()) {
            var isAdmin = false
            var isTeacher = false
            if (userTypeFormLens(webForm) == "admin") {
                isAdmin = true
            } else {
                isTeacher = true
            }
            val hashedPass = hashPassword(firstPass!!, settings.salt)
            users.add(User(UUID.randomUUID(), userNameFormLens(webForm), hashedPass, isAdmin, isTeacher))
            Response(Status.FOUND).header("Location", "/users")
        } else {
            Response(Status.OK).with(htmlView of ShowUserAddFVM(currentUser, webForm))
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}