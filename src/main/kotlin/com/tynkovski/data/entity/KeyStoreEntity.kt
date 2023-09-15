package com.tynkovski.data.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.BsonTimestamp
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class KeyStoreEntity(
    val userId: String,
    val accessKey: String,
    val refreshKey: String,
    @BsonId val id: String = ObjectId().toString(),
    @Contextual val createdAt: BsonTimestamp = BsonTimestamp(System.currentTimeMillis())
) {
    companion object {
        const val TABLE_NAME = "keysStore"
    }
}