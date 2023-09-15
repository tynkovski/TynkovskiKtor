package com.tynkovski.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val name: String,
    val password: String
)