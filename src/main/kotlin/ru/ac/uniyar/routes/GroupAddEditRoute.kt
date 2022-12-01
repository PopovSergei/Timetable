package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.group.Group
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ShowGroupAddEditFVM
import java.time.DayOfWeek
import java.util.UUID

fun groupAddEditRoute(
    currentUserLens: BiDiLens<Request, User?>,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showGroupAddForm(currentUserLens, groups, htmlView),
    "/" bind Method.POST to addGroupWithLens(currentUserLens, groups, htmlView)
)

private val groupIdLens = Query.string().optional("groupId")

private fun isGroupIdCorrect(groups: Groups, groupId: String?): Boolean {
    return groups.fetchString(groupId) != null
}

fun showGroupAddForm(
    currentUserLens: BiDiLens<Request, User?>,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val groupId = groupIdLens(request)

    if (currentUser?.isAdmin == true) {
        Response(Status.OK).with(htmlView of ShowGroupAddEditFVM(currentUser, groups.fetchString(groupId)))
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}

private val groupNameFormLens = FormField.string().required("groupName")
private val groupFormLens = Body.webForm(
    Validator.Feedback,
    groupNameFormLens
).toLens()

fun addGroupWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    groups: Groups,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = groupFormLens(request)
    val currentUser = currentUserLens(request)
    val groupId = groupIdLens(request)

    if (currentUser?.isAdmin == true) {
        if(webForm.errors.isEmpty() && !groups.hasSameName(groupNameFormLens(webForm))) {
            if (isGroupIdCorrect(groups, groupId)) {
                groups.update(Group(UUID.fromString(groupId), groupNameFormLens(webForm)))
                Response(Status.FOUND).header("Location", "/groups")
            } else {
                groups.add(Group(UUID.randomUUID(), groupNameFormLens(webForm)))
                Response(Status.FOUND).header("Location", "/groups")
            }
        } else {
            val newErrors = webForm.errors + Invalid(
                groupNameFormLens.meta.copy(description = "Group already has this name")
            )
            val newForm = webForm.copy(errors = newErrors)
            Response(Status.OK).with(htmlView of ShowGroupAddEditFVM(currentUser, groups.fetchString(groupId), newForm))
        }
    } else {
        Response(Status.NOT_ACCEPTABLE)
    }
}