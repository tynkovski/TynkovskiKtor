package com.tynkovski.controller

import com.tynkovski.data.datasource.KeyStoreDataSource
import com.tynkovski.data.datasource.UserDataSource
import com.tynkovski.model.exception.BadRequestException
import com.tynkovski.model.exception.NotFoundException
import com.tynkovski.model.exception.UnauthorizedException
import com.tynkovski.model.request.AuthRequest
import com.tynkovski.model.request.RefreshTokenRequest
import com.tynkovski.model.response.AccessTokenResponse
import com.tynkovski.model.response.AuthResponse
import com.tynkovski.model.response.MessageResponse
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.hashing.SaltedHash
import com.tynkovski.security.token.TokenService
import com.tynkovski.util.JWTPrincipalExtended
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthController(
    private val keyStoreDataSource: KeyStoreDataSource,
    private val userDataSource: UserDataSource,
    private val hashingService: HashingService,
    private val tokenService: TokenService
) {
    suspend fun login(call: ApplicationCall) {
        val request = call.receive<AuthRequest>()

        val user = userDataSource.getUserByLogin(request.login)
            ?: throw NotFoundException("User with login ${request.login} not found.")

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(user.password, user.salt)
        )

        if (!isValidPassword) {
            throw BadRequestException("Incorrect password")
        }

        val tokens = keyStoreDataSource.createTokens(user)

        call.respond(HttpStatusCode.OK, AuthResponse(tokens.accessToken, tokens.refreshToken))
    }

    suspend fun logout(call: ApplicationCall) {
        val userEntity = checkNotNull(call.principal<JWTPrincipalExtended>()).user
        val logoutRequest = call.receive<RefreshTokenRequest>()
        val refreshToken = logoutRequest.refreshToken

        val decodedRefreshToken = runCatching {
            tokenService.verifyRefreshToken(refreshToken)
        }.getOrElse { throw UnauthorizedException("Invalid token.") }

        val refreshKey = decodedRefreshToken.getClaim("key").asString()

        keyStoreDataSource.deleteRefreshToken(user = userEntity, refreshKey = refreshKey)

        call.respond(HttpStatusCode.OK, MessageResponse(message = "Logged out successfully."))
    }

    suspend fun refreshToken(call: ApplicationCall) {
        val authHeader = call.request.headers["Authorization"]
        if (authHeader == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val accessToken = authHeader.replace("Bearer\\s+".toRegex(), "")
            val refreshToken = refreshTokenRequest.refreshToken

            val decodedAccessToken = runCatching {
                tokenService.verifyAccessToken(accessToken, true)
            }.getOrElse { throw UnauthorizedException("Invalid access token.") }

            val decodedRefreshToken = runCatching {
                tokenService.verifyRefreshToken(refreshToken)
            }.getOrElse { throw UnauthorizedException("Invalid refresh token.") }

            val accessKey = decodedAccessToken.getClaim("key").asString()
            val refreshKey = decodedRefreshToken.getClaim("key").asString()

            if (accessKey == null || refreshKey == null) {
                throw UnauthorizedException("Invalid token.")
            }

            val userEntity = userDataSource.findUserByKey(accessKey = accessKey, refreshKey = refreshKey)
                ?: throw UnauthorizedException("Invalid token.")

            val newAccessToken = keyStoreDataSource.createAccessToken(user = userEntity, refreshKey = refreshKey)
            call.respond(HttpStatusCode.OK, AccessTokenResponse(accessToken = newAccessToken))
        }
    }
}

