package com.tynkovski.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val login: String,
    val name: String,
    val createdAt: Long
)