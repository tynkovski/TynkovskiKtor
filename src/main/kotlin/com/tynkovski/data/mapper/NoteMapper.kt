package com.tynkovski.data.mapper

import com.tynkovski.data.entity.NoteEntity
import com.tynkovski.model.request.NoteRequest
import com.tynkovski.model.response.NoteResponse

fun noteMapper(noteEntity: NoteEntity): NoteResponse = NoteResponse(
    id = noteEntity.id,
    text = noteEntity.text,
    color = noteEntity.color,
    title = noteEntity.title,
    createdAt = noteEntity.createdAt.value,
    updatedAt = noteEntity.updatedAt.value
)

fun noteMapper(userId: String, request: NoteRequest): NoteEntity = NoteEntity(
    ownerId = userId,
    text = request.text,
    title = request.title,
    color = request.color,
)

fun noteMapper(userId: String, noteId: String, request: NoteRequest): NoteEntity = NoteEntity(
    id = noteId,
    ownerId = userId,
    text = request.text,
    title = request.title,
    color = request.color,
)