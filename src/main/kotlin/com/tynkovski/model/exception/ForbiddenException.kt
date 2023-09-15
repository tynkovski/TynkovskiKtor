package com.tynkovski.model.exception

import io.ktor.http.HttpStatusCode

class ForbiddenException(msg: String? = null) : HttpException(msg) {
    override val statusCode = HttpStatusCode.Forbidden
}
