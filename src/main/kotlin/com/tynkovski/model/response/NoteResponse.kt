package com.tynkovski.model.response

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(
    val id: String,
    val text: String,
    val title: String?,
    val color: Long?,
    val createdAt: Long,
    val updatedAt: Long?,
)

@Serializable
data class NotesResponse(
    val count: Int,
    val notes: List<NoteResponse>
)
