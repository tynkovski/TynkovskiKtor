package com.tynkovski.model.exception

import io.ktor.http.HttpStatusCode

class UnauthorizedException(msg: String? = null) : HttpException(msg) {
    override val statusCode = HttpStatusCode.Unauthorized
}

