package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.database.repository.SchedulesDB
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
    schedulesDB: SchedulesDB,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showScheduleAddForm(currentUserLens, users, schedules, groups, htmlView),
    "/" bind Method.POST to addScheduleWithLens(currentUserLens, users, schedules, groups, schedulesDB, htmlView)
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
    schedules: Schedules,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val groupId = groupIdLens(request)
    val dayOfWeek = dayOfWeekLens(request)

    if (currentUser?.isAdmin == true) {
        if (isGroupIdCorrect(groups, groupId) && isDayOfWeekCorrect(dayOfWeek)) {
            Response(Status.OK).with(htmlView of ShowScheduleAddFVM(
                currentUser,
                "static",
                schedules.findLastClassNumber(groups.fetchString(groupId), DayOfWeek.valueOf(dayOfWeek!!)) + 1,
                null,
                null,
                users.fetchTeachers()))
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val classTypeFormLens = FormField.nonEmptyString().required("classType")
private val classNumberFormLens = FormField.int().required("classNumber")
private val classNameFormLens = FormField.string().required("className")
private val teacherIdFormLens = FormField.string().required("teacherId")
private val fractionClassNameFormLens = FormField.string().required("fractionClassName")
private val fractionTeacherIdFormLens = FormField.string().required("fractionTeacherId")
private val scheduleFormLens = Body.webForm(
    Validator.Feedback,
    classTypeFormLens,
    classNumberFormLens,
    classNameFormLens,
    teacherIdFormLens,
    fractionClassNameFormLens,
    fractionTeacherIdFormLens
).toLens()

fun addScheduleWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    users: Users,
    schedules: Schedules,
    groups: Groups,
    schedulesDB: SchedulesDB,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = scheduleFormLens(request)
    val currentUser = currentUserLens(request)
    val classNumber = classNumberFormLens(webForm)
    val teacher = users.fetchString(teacherIdFormLens(webForm))
    val fractionTeacher = users.fetchString(fractionTeacherIdFormLens(webForm))
    val groupId = groupIdLens(request)
    val dayOfWeekOrNot = dayOfWeekLens(request)

    if (currentUser?.isAdmin == true) {
        if (isGroupIdCorrect(groups, groupId) && isDayOfWeekCorrect(dayOfWeekOrNot)) {
            val group = groups.fetchString(groupId)
            val dayOfWeek = DayOfWeek.valueOf(dayOfWeekOrNot!!)

            if(webForm.errors.isEmpty() && !schedules.hasClassNumber(group, dayOfWeek, classNumber)) {
                schedules.add(Schedule(
                    UUID.randomUUID(),
                    group!!,
                    dayOfWeek,
                    classNumber,
                    classTypeFormLens(webForm),
                    classNameFormLens(webForm),
                    teacher,
                    fractionClassNameFormLens(webForm),
                    fractionTeacher
                    ), schedulesDB)
                Response(Status.FOUND).header("Location", "/schedule?groupId=$groupId")
            } else {
                val newErrors = webForm.errors + Invalid(
                    classNumberFormLens.meta.copy(description = "Class already exists")
                )
                val newForm = webForm.copy(errors = newErrors)

                Response(Status.OK).with(htmlView of ShowScheduleAddFVM(
                    currentUser,
                    classTypeFormLens(webForm),
                    classNumber,
                    teacher,
                    fractionTeacher,
                    users.fetchTeachers(),
                    newForm
                ))
            }
        } else {
            Response(Status.BAD_REQUEST)
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}