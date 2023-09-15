package com.tynkovski.data.datasource

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entity.NoteEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonTimestamp

interface NoteDataSource {

    suspend fun getNotesPaged(ownerId: String, sort: NoteEntity.Sort, page: Int, limit: Int): List<NoteEntity>

    suspend fun getNotes(ownerId: String, sort: NoteEntity.Sort): List<NoteEntity>

    suspend fun getNotes(ownerId: String, ids: List<String>, sort: NoteEntity.Sort = NoteEntity.Sort.ByDate(false)): List<NoteEntity>

    suspend fun getNote(ownerId: String, id: String): NoteEntity?

    suspend fun createNote(noteEntity: NoteEntity): Boolean

    suspend fun updateNote(ownerId: String, noteEntity: NoteEntity): Boolean

    suspend fun deleteNote(ownerId: String, noteEntity: NoteEntity): Boolean

    suspend fun deleteNotes(ownerId: String, list: List<NoteEntity>): Boolean

}

class NoteDataSourceImpl(database: MongoDatabase) : NoteDataSource {
    private val notes = database.getCollection<NoteEntity>(NoteEntity.TABLE_NAME)

    private suspend fun getSorted(flow: FindFlow<NoteEntity>, ascending: Boolean, fieldName: String): List<NoteEntity> {
        val sort = if (ascending) Sorts.ascending(fieldName) else Sorts.descending(fieldName)
        return flow.sort(sort).toList()
    }

    override suspend fun getNotesPaged(ownerId: String, sort: NoteEntity.Sort, page: Int, limit: Int): List<NoteEntity> {
        val filters = Filters.eq(NoteEntity::ownerId.name, ownerId)

        val ownerNotes = notes
            .find(filters)
            .skip(page * limit)
            .limit(limit)
            .partial(true)

        return getSorted(ownerNotes, sort.isAscending, sort.toString())
    }

    override suspend fun getNotes(ownerId: String, sort: NoteEntity.Sort): List<NoteEntity> {
        val filters = Filters.eq(NoteEntity::ownerId.name, ownerId)

        val ownerNotes = notes.find(filters)

        return getSorted(ownerNotes, sort.isAscending, sort.toString())
    }

    override suspend fun getNotes(ownerId: String, ids: List<String>, sort: NoteEntity.Sort): List<NoteEntity> {
        val filters = Filters.and(
            Filters.eq(NoteEntity::ownerId.name, ownerId),
            Filters.`in`("_id", ids)
        )

        val ownerNotes = notes.find(filters)

        return getSorted(ownerNotes, sort.isAscending, sort.toString())
    }

    override suspend fun getNote(ownerId: String, id: String): NoteEntity? {
        val filters = Filters.and(
            Filters.eq(NoteEntity::ownerId.name, ownerId),
            Filters.eq("_id", id)
        )

        return notes
            .find(filters)
            .firstOrNull()
    }

    override suspend fun createNote(noteEntity: NoteEntity): Boolean {
        return notes.insertOne(noteEntity).wasAcknowledged()
    }

    override suspend fun updateNote(
        ownerId: String,
        noteEntity: NoteEntity
    ): Boolean {
        val updates = listOf(
            Updates.set(NoteEntity::text.name, noteEntity.text),
            Updates.set(NoteEntity::title.name, noteEntity.title),
            Updates.set(NoteEntity::color.name, noteEntity.color),
            Updates.set(NoteEntity::updatedAt.name, BsonTimestamp(System.currentTimeMillis())),
        )

        val filters = Filters.and(
            Filters.eq(NoteEntity::ownerId.name, ownerId),
            Filters.eq("_id", noteEntity.id)
        )

        return notes.updateOne(filters, updates).wasAcknowledged()
    }

    override suspend fun deleteNote(ownerId: String, noteEntity: NoteEntity): Boolean {
        val filters = Filters.and(
            Filters.eq(NoteEntity::ownerId.name, ownerId),
            Filters.eq("_id", noteEntity.id)
        )

        return notes.deleteOne(filters).wasAcknowledged()
    }

    override suspend fun deleteNotes(ownerId: String, list: List<NoteEntity>): Boolean {
        val ids = list.map { it.id }

        val filters = Filters.and(
            Filters.eq(NoteEntity::ownerId.name, ownerId),
            Filters.`in`("_id", ids)
        )

        return notes.deleteMany(filters).wasAcknowledged()
    }
}