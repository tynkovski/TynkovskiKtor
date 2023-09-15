package com.tynkovski.data.datasource

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entity.KeyStoreEntity
import com.tynkovski.data.entity.UserEntity
import com.tynkovski.model.exception.ConflictException
import com.tynkovski.model.exception.UnauthorizedException
import com.tynkovski.model.response.TokenResponse
import com.tynkovski.security.token.TokenClaim
import com.tynkovski.security.token.TokenService
import kotlinx.coroutines.flow.firstOrNull

interface KeyStoreDataSource {
    suspend fun createTokens(user: UserEntity): TokenResponse

    suspend fun createAccessToken(user: UserEntity, refreshKey: String): String

    suspend fun deleteRefreshToken(user: UserEntity, refreshKey: String): Unit
}

class KeyStoreDataSourceImpl(
    database: MongoDatabase,
    private val tokenService: TokenService
) : KeyStoreDataSource {
    private val keys = database.getCollection<KeyStoreEntity>(KeyStoreEntity.TABLE_NAME)

    override suspend fun createTokens(user: UserEntity): TokenResponse {
        val randAccessKey = tokenService.generateKey()
        val randRefreshKey = tokenService.generateKey()

        val keyStoreEntity = KeyStoreEntity(
            userId = user.id,
            accessKey = randAccessKey,
            refreshKey = randRefreshKey
        )

        val result = keys.insertOne(keyStoreEntity)

        if (result.wasAcknowledged()) {
            val tokenClaim = TokenClaim.create(user)

            val accessToken = tokenService.generateAccessToken(randAccessKey, tokenClaim)
            val refreshToken = tokenService.generateRefreshToken(randRefreshKey, tokenClaim)

            return TokenResponse(accessToken, refreshToken)
        }

        throw ConflictException("Can't create keys.")
    }

    private suspend fun getKeyStore(userId: String, refreshKey: String): KeyStoreEntity {
        val filters = Filters.and(
            Filters.eq(KeyStoreEntity::userId.name, userId),
            Filters.eq(KeyStoreEntity::refreshKey.name, refreshKey),
        )

        return keys.find(filters).firstOrNull()
            ?: throw UnauthorizedException("Invalid refresh token.")
    }

    override suspend fun createAccessToken(user: UserEntity, refreshKey: String): String {
        val keyStore = getKeyStore(user.id, refreshKey)
        val randAccessKey = tokenService.generateKey()

        val updates = listOf(Updates.set(KeyStoreEntity::accessKey.name, randAccessKey))

        val filters = Filters.eq("_id", keyStore.id)

        val wasAcknowledged = keys.updateOne(filters, updates).wasAcknowledged()
        if (!wasAcknowledged) {
            throw ConflictException("Can't create keys.")
        }

        val tokenClaim = TokenClaim.create(user)
        return tokenService.generateAccessToken(randAccessKey, tokenClaim)
    }

    override suspend fun deleteRefreshToken(user: UserEntity, refreshKey: String) {
        val keyStore = getKeyStore(user.id, refreshKey)

        val filters = Filters.eq("_id", keyStore.id)

        val wasAcknowledged = keys.deleteOne(filters).wasAcknowledged()
        if (!wasAcknowledged) {
            throw ConflictException("Can't delete keys.")
        }
    }
}