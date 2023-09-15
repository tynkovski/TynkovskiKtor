package com.tynkovski.di

import com.tynkovski.security.token.JwtTokenService
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.server.config.*
import org.koin.dsl.module

val tokenModule = module {
    single<TokenConfig> { (config: ApplicationConfig) ->
        TokenConfig(
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            accessSecret = System.getenv("JWT_SECRET"),
            refreshSecret = System.getenv("JWT_SECRET")
        )
    }

    single<TokenService> { (config: TokenConfig) ->
        JwtTokenService(config)
    }
}