package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.AdminVM
import ru.ac.uniyar.models.TeacherVM

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