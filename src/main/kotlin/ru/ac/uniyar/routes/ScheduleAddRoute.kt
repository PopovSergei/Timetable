package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.models.ShowScheduleAddFVM
import java.time.DayOfWeek
import java.util.UUID

fun scheduleAddRoute(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showScheduleAddForm(currentUserLens, users, groups, htmlView),
    "/" bind Method.POST to addScheduleWithLens(currentUserLens, users, schedules, groups, htmlView)
)

private val groupIdLens = Query.string().optional("group")
private val dayOfWeekLens = Query.string().optional("dayOfWeek")

private fun isDayOfWeekCorrect(dayOfWeek: String?): Boolean {
    try {
        if (dayOfWeek != null) {
            DayOfWeek.valueOf(dayOfWeek)
        } else {
            return false
        }
    } catch (e: IllegalArgumentException) {
        return false
    }
    return true
}
private fun isGroupIdCorrect(groups: Groups, groupId: String?): Boolean {
    return groups.fetchString(groupId) != null
}

fun showScheduleAddForm(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val groupId = groupIdLens(request)
    val dayOfWeek = dayOfWeekLens(request)

    if (currentUser?.isAdmin == true) {
        if (isGroupIdCorrect(groups, groupId) && isDayOfWeekCorrect(dayOfWeek)) {
            Response(Status.OK).with(htmlView of ShowScheduleAddFVM(currentUser, users.fetchTeachers()))
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

fun addScheduleWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = scheduleFormLens(request)
    val currentUser = currentUserLens(request)
    val teacher = users.fetchString(teacherIdFormLens(webForm))
    val groupId = groupIdLens(request)
    val dayOfWeek = dayOfWeekLens(request)

    if (currentUser?.isAdmin == true) {
        if (isGroupIdCorrect(groups, groupId) && isDayOfWeekCorrect(dayOfWeek)) {
            if(webForm.errors.isEmpty()) {
                if (teacher != null) {
                    schedules.add(Schedule(
                        UUID.randomUUID(),
                        groups.fetchString(groupId)!!,
                        DayOfWeek.valueOf(dayOfWeek!!),
                        schedules.findLastClassNumber(groups.fetchString(groupId), DayOfWeek.valueOf(dayOfWeek)) + 1,
                        classNameFormLens(webForm),
                        teacher))
                } else {
                    schedules.add(Schedule(
                        UUID.randomUUID(),
                        groups.fetchString(groupId)!!,
                        DayOfWeek.valueOf(dayOfWeek!!),
                        schedules.findLastClassNumber(groups.fetchString(groupId), DayOfWeek.valueOf(dayOfWeek)) + 1,
                        classNameFormLens(webForm),
                        null))
                }
                Response(Status.FOUND).header("Location", "/schedule?groupId=$groupId")
            } else {
                Response(Status.OK).with(htmlView of ShowScheduleAddFVM(currentUser, users.fetchTeachers()))
            }
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}