package com.tynkovski.plugin

import com.tynkovski.controller.UserController
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import com.tynkovski.util.JWTPrincipalExtended
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val tokenConfig by inject<TokenConfig> { parametersOf(environment.config) }
    val tokenService by inject<TokenService> { parametersOf(tokenConfig) }
    val myRealm = environment.config.property("jwt.realm").getString()

    val userController by inject<UserController>()

    install(Authentication) {
        jwt {
            realm = myRealm

            verifier(tokenService.accessTokenVerifier())

            validate { credential ->
                val payload = credential.payload
                val accessKey = payload.getClaim("key").asString()
                val user = userController.findUserForAccessKey(accessKey)

                if (user != null && payload.audience.contains(tokenConfig.audience)) {
                    JWTPrincipalExtended(payload, user)
                } else {
                    null
                }
            }
        }
    }
}
