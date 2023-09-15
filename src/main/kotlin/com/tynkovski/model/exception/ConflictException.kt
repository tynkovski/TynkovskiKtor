package com.tynkovski.model.exception

import io.ktor.http.HttpStatusCode

class ConflictException(msg: String? = null) : HttpException(msg) {
    override val statusCode = HttpStatusCode.Conflict
}
