package com.tynkovski.model.exception

import com.tynkovski.model.response.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

abstract class HttpException(
    msg: String?
) : Exception(msg) {
    abstract val statusCode: HttpStatusCode

    suspend fun respond(call: ApplicationCall) {
        call.respond(statusCode, ErrorResponse(message = message ?: statusCode.description))
    }
}