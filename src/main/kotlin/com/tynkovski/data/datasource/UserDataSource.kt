package com.tynkovski.data.datasource

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entity.KeyStoreEntity
import com.tynkovski.data.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

interface UserDataSource {
    suspend fun getUserByLogin(login: String): UserEntity?

    suspend fun createUser(userEntity: UserEntity): Boolean
    suspend fun deleteUser(userEntity: UserEntity): Boolean
    suspend fun changeName(user: UserEntity, name: String): Boolean
    suspend fun changePassword(user: UserEntity, newPassword: String, newSalt: String): Boolean

    suspend fun findUserByKey(accessKey: String): UserEntity?
    suspend fun findUserByKey(accessKey: String, refreshKey: String): UserEntity?

    suspend fun getUserById(id: String): UserEntity?
    suspend fun deleteUserById(id: String): Boolean
    suspend fun changeNameById(id: String, name: String): Boolean
    suspend fun changePasswordById(id: String, newPassword: String, newSalt: String): Boolean
}

class UserDataSourceImpl(database: MongoDatabase) : UserDataSource {

    private val users = database.getCollection<UserEntity>(UserEntity.TABLE_NAME)
    private val keyStore = database.getCollection<KeyStoreEntity>(KeyStoreEntity.TABLE_NAME)


    override suspend fun getUserByLogin(login: String): UserEntity? {
        return users.find(Filters.eq(UserEntity::login.name, login)).firstOrNull()
    }

    override suspend fun createUser(userEntity: UserEntity): Boolean {
        if (getUserByLogin(userEntity.login) != null) return false
        return users.insertOne(userEntity).wasAcknowledged()
    }

    override suspend fun deleteUser(userEntity: UserEntity): Boolean {
        return deleteUserById(userEntity.id)
    }

    override suspend fun changeName(user: UserEntity, name: String): Boolean {
        return changeNameById(user.id, name)
    }

    override suspend fun changePassword(user: UserEntity, newPassword: String, newSalt: String): Boolean {
        return changePasswordById(user.id, newPassword, newSalt)
    }

    override suspend fun findUserByKey(accessKey: String): UserEntity? {
        val filters = Filters.eq(KeyStoreEntity::accessKey.name, accessKey)
        val key = keyStore.find(filters).first()
        return getUserById(key.userId)
    }

    override suspend fun findUserByKey(accessKey: String, refreshKey: String): UserEntity? {
        val filters = Filters.and(
            Filters.eq(KeyStoreEntity::accessKey.name, accessKey),
            Filters.eq(KeyStoreEntity::refreshKey.name, refreshKey)
        )
        val key = keyStore.find(filters).first()
        return getUserById(key.userId)
    }

    override suspend fun getUserById(id: String): UserEntity? {
        return users.find(Filters.eq("_id", id)).firstOrNull()
    }

    override suspend fun deleteUserById(id: String): Boolean {
        return users.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
    }

    override suspend fun changeNameById(id: String, name: String): Boolean {
        val filters = Filters.eq("_id", id)
        val updates = listOf(Updates.set(UserEntity::name.name, name))
        return users.updateOne(filters, updates).wasAcknowledged()
    }

    override suspend fun changePasswordById(id: String, newPassword: String, newSalt: String): Boolean {
        val filters = Filters.eq("_id", id)
        val updates = listOf(
            Updates.set(UserEntity::password.name, newPassword),
            Updates.set(UserEntity::salt.name, newSalt),
        )
        return users.updateOne(filters, updates).wasAcknowledged()
    }
}