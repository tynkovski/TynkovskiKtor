package com.tynkovski.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val name: String
)