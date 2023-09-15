package com.tynkovski.model.exception

import io.ktor.http.HttpStatusCode

class RateLimitReachedException(msg: String? = null) : HttpException(msg) {
    override val statusCode = HttpStatusCode.Forbidden
}