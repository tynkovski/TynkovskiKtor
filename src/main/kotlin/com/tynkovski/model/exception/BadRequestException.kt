package com.tynkovski.model.exception

import io.ktor.http.HttpStatusCode

class BadRequestException(msg: String? = null) : HttpException(msg) {
    override val statusCode = HttpStatusCode.BadRequest
}
