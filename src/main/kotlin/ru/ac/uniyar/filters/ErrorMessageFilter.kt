package ru.ac.uniyar.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.lens.BiDiLens
import org.http4k.template.TemplateRenderer
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ErrorAccessVM
import ru.ac.uniyar.models.ErrorInfoVM

fun showErrorMessageFilter(
    currentUserLens: BiDiLens<Request, User?>,
    renderer: TemplateRenderer
): Filter = Filter { next: HttpHandler ->
    { request: Request ->
        val response = next(request)
        if (response.status.successful) {
            response
        } else {
            val currentUser = currentUserLens(request)
            if (response.status.code == 406) {
                response.body(renderer(ErrorAccessVM(currentUser,request.uri)))
            } else {
                response.body(renderer(ErrorInfoVM(currentUser, request.uri)))
            }
        }
    }
}