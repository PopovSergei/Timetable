package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.models.ShowScheduleEditFVM

fun scheduleCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showScheduleEditForm(currentUserLens, users, schedules, htmlView),
    "/" bind Method.POST to editScheduleWithLens(currentUserLens, users, schedules, htmlView)
)

fun showScheduleEditForm(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val scheduleId = request.path("id").orEmpty()

    if (currentUser?.isAdmin == true) {
        if (schedules.fetchString(scheduleId) != null) {
            Response(Status.OK).with(htmlView of ShowScheduleEditFVM(currentUser, schedules.fetchString(scheduleId), users.fetchTeachers()))
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val classTypeFormLens = FormField.string().required("classType")
private val classNameFormLens = FormField.string().required("className")
private val teacherIdFormLens = FormField.string().required("teacherId")
private val fractionClassNameFormLens = FormField.string().required("fractionClassName")
private val fractionTeacherIdFormLens = FormField.string().required("fractionTeacherId")
private val scheduleFormLens = Body.webForm(
    Validator.Feedback,
    classTypeFormLens,
    classNameFormLens,
    teacherIdFormLens,
    fractionClassNameFormLens,
    fractionTeacherIdFormLens,
).toLens()

fun editScheduleWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = scheduleFormLens(request)
    val currentUser = currentUserLens(request)
    val scheduleId = request.path("id").orEmpty()
    val teacher = users.fetchString(teacherIdFormLens(webForm))
    val fractionTeacher = users.fetchString(fractionTeacherIdFormLens(webForm))

    if (currentUser?.isAdmin == true) {
        if (schedules.fetchString(scheduleId) != null) {
            if(webForm.errors.isEmpty()) {
                val schedule = schedules.fetchString(scheduleId)
                schedules.update(
                    Schedule(
                        schedule!!.id,
                        schedule.group,
                        schedule.dayOfWeek,
                        schedule.classNumber,
                        classTypeFormLens(webForm),
                        classNameFormLens(webForm),
                        teacher,
                        fractionClassNameFormLens(webForm),
                        fractionTeacher
                    )
                )
                val groupId = schedules.fetchString(scheduleId)!!.group.id
                Response(Status.FOUND).header("Location", "/schedule?groupId=$groupId")
            } else {
                Response(Status.OK).with(htmlView of ShowScheduleEditFVM(currentUser, schedules.fetchString(scheduleId), users.fetchTeachers(), webForm))
            }
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}