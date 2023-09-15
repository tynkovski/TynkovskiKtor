package com.tynkovski.plugin

import com.tynkovski.model.exception.HttpException
import io.ktor.http.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*
import io.ktor.server.request.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(StatusPages) {
        exception<HttpException> { call, cause -> cause.respond(call) }

        exception<Exception> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.stackTraceToString())
        }
    }
}
