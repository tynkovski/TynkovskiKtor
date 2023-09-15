package com.tynkovski.model.exception

import io.ktor.http.HttpStatusCode

class NotFoundException(msg: String? = null) : HttpException(msg) {
    override val statusCode = HttpStatusCode.NotFound
}
