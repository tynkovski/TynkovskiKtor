package com.tynkovski.controller

import com.tynkovski.data.datasource.KeyStoreDataSource
import com.tynkovski.data.datasource.UserDataSource
import com.tynkovski.data.entity.UserEntity
import com.tynkovski.data.mapper.userMapper
import com.tynkovski.model.exception.BadRequestException
import com.tynkovski.model.exception.ConflictException
import com.tynkovski.model.request.ChangePasswordRequest
import com.tynkovski.model.request.EditUserRequest
import com.tynkovski.model.request.RegisterRequest
import com.tynkovski.model.response.AuthResponse
import com.tynkovski.model.response.MessageResponse
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.hashing.SaltedHash
import com.tynkovski.util.JWTPrincipalExtended
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UserController(
    private val userDataSource: UserDataSource,
    private val keyStoreDataSource: KeyStoreDataSource,
    private val hashingService: HashingService
) {
    private fun validatePassword(password: String) = runCatching {
        require(password.length >= 8) { "Password is too short" }
        require(password.length <= 32) { "Password is too long" }
        require(password.none { it.isWhitespace() }) { "Whitespaces are not allowed" }
        require(password.any { it.isDigit() }) { "Should be at least one digit" }
        require(password.any { it.isUpperCase() }) { "Should be at least one uppercase letter" }
        require(password.any { !it.isLetterOrDigit() }) { "Should be at least one special character" }
    }

    suspend fun findUserForAccessKey(accessKey: String): UserEntity? {
        return userDataSource.findUserByKey(accessKey = accessKey)
    }

    suspend fun register(call: ApplicationCall) {
        val request = call.receive<RegisterRequest>()

        validatePassword(request.password).getOrElse { throw BadRequestException(it.message) }

        val saltedHash = hashingService.generateSaltedHash(request.password)

        val user = UserEntity(
            login = request.login,
            name = request.name,
            password = saltedHash.hash,
            salt = saltedHash.salt,
        )

        val wasAcknowledged = userDataSource.createUser(user)

        if (wasAcknowledged) {
            val tokens = keyStoreDataSource.createTokens(user)
            call.respond(HttpStatusCode.OK, AuthResponse(tokens.accessToken, tokens.refreshToken))
        } else {
            throw ConflictException("User with email ${request.login} already exists.")
        }
    }

    suspend fun getUser(call: ApplicationCall) {
        val userEntity = checkNotNull(call.principal<JWTPrincipalExtended>()).user

        call.respond(HttpStatusCode.OK, userMapper(userEntity))
    }

    suspend fun deleteUser(call: ApplicationCall) {
        val userEntity = checkNotNull(call.principal<JWTPrincipalExtended>()).user

        val wasAcknowledged = userDataSource.deleteUser(userEntity)

        if (wasAcknowledged) {
            call.respond(HttpStatusCode.OK, MessageResponse("User deleted successfully."))
        } else {
            throw ConflictException("Can not delete user ${userEntity.login}.")
        }
    }

    suspend fun changePassword(call: ApplicationCall) {
        val request = call.receive<ChangePasswordRequest>()

        val userEntity = checkNotNull(call.principal<JWTPrincipalExtended>()).user

        val isValidPassword = hashingService.verify(
            value = request.oldPassword,
            saltedHash = SaltedHash(userEntity.password, userEntity.salt)
        )

        if (isValidPassword) {
            val saltedHash = hashingService.generateSaltedHash(request.newPassword)

            val wasAcknowledged = userDataSource.changePassword(userEntity, saltedHash.hash, saltedHash.salt)

            if (wasAcknowledged) {
                call.respond(HttpStatusCode.OK, MessageResponse("Password changed successfully."))
            } else {
                throw ConflictException("Can not change password.")
            }
        } else {
            throw BadRequestException("Invalid password")
        }
    }

    suspend fun editUser(call: ApplicationCall) {
        val request = call.receive<EditUserRequest>()
        val userEntity = checkNotNull(call.principal<JWTPrincipalExtended>()).user

        val wasAcknowledged = userDataSource.changeName(userEntity, request.name)

        if (wasAcknowledged) {
            call.respond(HttpStatusCode.OK, MessageResponse("User edit successfully."))
        } else {
            throw ConflictException("Editing user error")
        }
    }
}