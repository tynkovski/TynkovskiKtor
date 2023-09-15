package com.tynkovski.model.request

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val text: String,
    val title: String = "",
    val color: Long = -1L
)
