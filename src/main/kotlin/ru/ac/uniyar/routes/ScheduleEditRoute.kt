package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.teacher.Teachers
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ShowScheduleFVM
import java.time.DayOfWeek
import java.util.*

fun scheduleCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    schedules: Schedules,
    teachers: Teachers,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showScheduleForm(currentUserLens, currentAccessLens, schedules, teachers, htmlView),
    "/" bind Method.POST to addAnimalWithLens(currentUserLens, currentAccessLens, schedules, teachers, groups, htmlView)
)

private val scheduleIdLens = Query.string().optional("scheduleId")
private val groupIdLens = Query.string().optional("group")
private val dayOfWeekLens = Query.string().optional("day")

fun showScheduleForm(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    schedules: Schedules,
    teachers: Teachers,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    val scheduleId = scheduleIdLens(request)
    val groupId = groupIdLens(request)
    val dayOfWeek = dayOfWeekLens(request)

    if (currentAccess == "admin") {
        if (schedules.isSchedule(scheduleId!!)) {
            val scheduleToEdit = schedules.fetch(UUID.fromString(scheduleId))
            Response(Status.OK).with(htmlView of ShowScheduleFVM(currentUser, scheduleToEdit!!, teachers.fetchAll()))
        } else {
            if (groupId != null && dayOfWeek != null)
                Response(Status.OK).with(htmlView of ShowScheduleFVM(currentUser, null, teachers.fetchAll()))
            else {
                Response(Status.BAD_REQUEST)
            }
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val classNameFormLens = FormField.string().required("className")
private val teacherIdFormLens = FormField.uuid().required("teacherId")
private val scheduleFormLens = Body.webForm(
    Validator.Feedback,
    classNameFormLens,
    teacherIdFormLens,
).toLens()

fun addAnimalWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    schedules: Schedules,
    teachers: Teachers,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = scheduleFormLens(request)
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    val scheduleId = scheduleIdLens(request)
    val groupId = groupIdLens(request)
    val dayOfWeek = dayOfWeekLens(request)

    if (currentAccess == "admin") {
        if(webForm.errors.isEmpty()) {
            if (schedules.isSchedule(scheduleId!!)) {
                schedules.update(schedules.fetch(UUID.fromString(scheduleId))!!, classNameFormLens(webForm), teachers.fetch(teacherIdFormLens(webForm))!!)

                Response(Status.FOUND).header("Location", "/schedule")
            } else {
                //TODO: исправить добавление элемента
                schedules.add(
                    Schedule(
                    UUID.randomUUID(),
                    groups.fetch(UUID.fromString(groupId))!!,
                    DayOfWeek.valueOf(dayOfWeek!!),
                    5, //TODO: находить номер занятия
                    classNameFormLens(webForm),
                    teachers.fetch(teacherIdFormLens(webForm))!!)
                )
                Response(Status.FOUND).header("Location", "/schedule")
            }
        } else {
            Response(Status.OK).with(htmlView of ShowScheduleFVM(currentUser, null, teachers.fetchAll(), webForm))
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}