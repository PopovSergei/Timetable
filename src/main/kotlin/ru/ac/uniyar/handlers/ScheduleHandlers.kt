package ru.ac.uniyar.handlers


import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.lens.Query
import org.http4k.lens.string
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ScheduleVM

class ShowScheduleHandler(
    private val schedules: Schedules,
    private val groups: Groups,
    private val currentUserLens: BiDiLens<Request, User?>,
    private val currentAccessLens: BiDiLens<Request, String?>,
    private val htmlView: BiDiBodyLens<ViewModel>
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        val currentAccess = currentAccessLens(request)
        val groupLens = Query.string().optional("group")
        val group = groupLens(request)

        var monday : List<Schedule> = emptyList()
        var tuesday : List<Schedule> = emptyList()
        var wednesday : List<Schedule> = emptyList()
        var thursday : List<Schedule> = emptyList()
        var friday : List<Schedule> = emptyList()
        var saturday : List<Schedule> = emptyList()

        if (group != null) {
            monday = schedules.filterGroupMonday(group)
            tuesday = schedules.filterGroupTuesday(group)
            wednesday = schedules.filterGroupWednesday(group)
            thursday = schedules.filterGroupThursday(group)
            friday = schedules.filterGroupFriday(group)
            saturday = schedules.filterGroupSaturday(group)
            return Response(Status.OK).with(htmlView of ScheduleVM(
                currentUser,
                currentAccess,
                groups.fetchAll(),
                group,
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday))
        }
        return Response(Status.OK).with(htmlView of ScheduleVM(
            currentUser,
            currentAccess,
            groups.fetchAll(),
            null,
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday
        ))
    }
}