package com.tynkovski.security.token

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val accessSecret: String,
    val refreshSecret: String
)
