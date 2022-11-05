package ru.ac.uniyar.handlers


import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.lens.Query
import org.http4k.lens.string
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ScheduleVM

class ShowScheduleHandler(
    private val schedules: Schedules,
    private val groups: Groups,
    private val currentUserLens: BiDiLens<Request, User?>,
    private val htmlView: BiDiBodyLens<ViewModel>
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        val groupLens = Query.string().optional("groupId")
        val groupId = groupLens(request)

        var monday : List<Schedule> = emptyList()
        var tuesday : List<Schedule> = emptyList()
        var wednesday : List<Schedule> = emptyList()
        var thursday : List<Schedule> = emptyList()
        var friday : List<Schedule> = emptyList()
        var saturday : List<Schedule> = emptyList()
        var group: Group? = null

        if (groupId != null)
            group = groups.fetchString(groupId)

        if (group != null) {
            monday = schedules.filterGroupMonday(group.id)
            tuesday = schedules.filterGroupTuesday(group.id)
            wednesday = schedules.filterGroupWednesday(group.id)
            thursday = schedules.filterGroupThursday(group.id)
            friday = schedules.filterGroupFriday(group.id)
            saturday = schedules.filterGroupSaturday(group.id)
            return Response(Status.OK).with(htmlView of ScheduleVM(
                currentUser,
                groups.fetchAll(),
                group.name,
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday))
        }
        return Response(Status.OK).with(htmlView of ScheduleVM(
            currentUser,
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