package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.models.AdminVM
import ru.ac.uniyar.models.TeacherVM
import ru.ac.uniyar.models.UsersVM

class ShowUserHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val schedules: Schedules,
    private val htmlView: BiDiBodyLens<ViewModel>
    ): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        if (currentUser != null) {
            if (currentUser.isAdmin) {
                return Response(Status.OK).with(htmlView of AdminVM(currentUser))
            } else {
                if (currentUser.isTeacher) {
                    return Response(Status.OK).with(htmlView of TeacherVM(currentUser, schedules.filterTeacherSchedule(currentUser)))
                }
            }
        }
        return Response(Status.NOT_ACCEPTABLE)
    }
}

class ShowUsersHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val users: Users,
    private val htmlView: BiDiBodyLens<ViewModel>
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        return if (currentUser?.isAdmin == true) {
            Response(Status.OK).with(htmlView of UsersVM(currentUser, users.fetchAll()))
        } else {
            Response(Status.NOT_ACCEPTABLE)
        }
    }
}

class UserRemoveHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val users: Users,
    private val schedules: Schedules,
    private val usersDB: UsersDB
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        val userId = request.path("id").orEmpty()
        val user = users.fetchString(userId)

        return if (currentUser?.isAdmin == true && currentUser != user) {
            if (user != null) {
                schedules.removeTeacher(user)
                users.remove(user, usersDB)
                Response(Status.FOUND).header("Location", "/users")
            } else {
                Response(Status.BAD_REQUEST)
            }
        } else {
            Response(Status.NOT_ACCEPTABLE)
        }
    }
}