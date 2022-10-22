package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.teacher.Teachers
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ShowScheduleFVM

fun scheduleCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    schedules: Schedules,
    teachers: Teachers,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showScheduleEditForm(currentUserLens, currentAccessLens, schedules, teachers, htmlView),
    "/" bind Method.POST to editScheduleWithLens(currentUserLens, currentAccessLens, schedules, teachers, htmlView)
)

fun showScheduleEditForm(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    schedules: Schedules,
    teachers: Teachers,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    val scheduleId = request.path("id").orEmpty()

    if (currentAccess == "admin") {
        if (schedules.fetchString(scheduleId) != null) {
            Response(Status.OK).with(htmlView of ShowScheduleFVM(currentUser, schedules.fetchString(scheduleId), teachers.fetchAll()))
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val classNameFormLens = FormField.string().required("className")
private val teacherIdFormLens = FormField.string().required("teacherId")
private val scheduleFormLens = Body.webForm(
    Validator.Feedback,
    classNameFormLens,
    teacherIdFormLens,
).toLens()

fun editScheduleWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    schedules: Schedules,
    teachers: Teachers,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = scheduleFormLens(request)
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    val scheduleId = request.path("id").orEmpty()
    val teacher = teachers.fetchString(teacherIdFormLens(webForm))

    if (currentAccess == "admin") {
        if (schedules.fetchString(scheduleId) != null) {
            if(webForm.errors.isEmpty()) {
                if (teacher != null) {
                    schedules.update(
                        schedules.fetchString(scheduleId)!!,
                        classNameFormLens(webForm),
                        teacher)
                } else {
                    schedules.update(
                        schedules.fetchString(scheduleId)!!,
                        classNameFormLens(webForm),
                        null)
                }
                val groupId = schedules.fetchString(scheduleId)!!.group.id
                Response(Status.FOUND).header("Location", "/schedule?groupId=$groupId")
            } else {
                Response(Status.OK).with(htmlView of ShowScheduleFVM(currentUser, schedules.fetchString(scheduleId), teachers.fetchAll(), webForm))
            }
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}