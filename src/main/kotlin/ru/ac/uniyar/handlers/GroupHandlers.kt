package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.ac.uniyar.database.repository.GroupsDB
import ru.ac.uniyar.database.repository.SchedulesDB
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.GroupsVM

class ShowGroupsHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val groups: Groups,
    private val htmlView: BiDiBodyLens<ViewModel>
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        return if (currentUser?.isAdmin == true) {
            Response(Status.OK).with(htmlView of GroupsVM(currentUser, groups.fetchAll()))
        } else {
            Response(Status.NOT_ACCEPTABLE)
        }
    }
}

class GroupRemoveHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val schedules: Schedules,
    private val groups: Groups,
    private val schedulesDB: SchedulesDB,
    private val groupsDB: GroupsDB,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        val groupId = request.path("id").orEmpty()
        val group = groups.fetchString(groupId)

        return if (currentUser?.isAdmin == true) {
            if (group != null) {
                schedules.removeGroup(group, schedulesDB)
                groups.remove(group, groupsDB)
                Response(Status.FOUND).header("Location", "/groups")
            } else {
                Response(Status.BAD_REQUEST)
            }
        } else {
            Response(Status.NOT_ACCEPTABLE)
        }
    }
}