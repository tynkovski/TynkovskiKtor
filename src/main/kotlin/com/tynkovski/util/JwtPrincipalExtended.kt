package com.tynkovski.util

import com.auth0.jwt.interfaces.Payload
import com.tynkovski.data.entity.UserEntity
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class JWTPrincipalExtended(payload: Payload, val user: UserEntity) : Principal, JWTPayloadHolder(payload)
