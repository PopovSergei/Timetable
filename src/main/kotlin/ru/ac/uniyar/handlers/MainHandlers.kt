package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.MainVM

class ShowMainHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val htmlView: BiDiBodyLens<ViewModel>
    ): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        return Response(Status.OK).with(htmlView of MainVM(currentUser))
    }
}

class RedirectToMainHandler: HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.FOUND).header("Location", "/main")
    }
}