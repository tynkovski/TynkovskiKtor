package com.tynkovski.data.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.BsonTimestamp
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class NoteEntity(
    val text: String,
    val ownerId: String,
    val title: String = "",
    val color: Long = -1,
    @BsonId val id: String = ObjectId().toString(),
    @Contextual val createdAt: BsonTimestamp = BsonTimestamp(System.currentTimeMillis()),
    @Contextual val updatedAt: BsonTimestamp = BsonTimestamp(-1L)
) {
    companion object {
        const val TABLE_NAME = "notes"
    }

    sealed class Sort(val isAscending: Boolean) {
        companion object {
            fun fromString(string: String?): Sort {
                return when (string) {
                    "TITLE_ASC" -> ByTitle(true)
                    "TEXT_ASC" -> ByText(true)
                    "DATE_ASC" -> ByDate(true)
                    "TITLE_DESC" -> ByTitle(false)
                    "TEXT_DESC" -> ByText(false)
                    "DATE_DESC" -> ByDate(false)
                    else -> ByDate(false)
                }
            }
        }

        override fun toString(): String {
            return when (this) {
                is ByDate -> NoteEntity::createdAt.name
                is ByText -> NoteEntity::text.name
                is ByTitle -> NoteEntity::title.name
            }
        }

        class ByDate(isAscending: Boolean) : Sort(isAscending)
        class ByTitle(isAscending: Boolean) : Sort(isAscending)
        class ByText(isAscending: Boolean) : Sort(isAscending)
    }
}
