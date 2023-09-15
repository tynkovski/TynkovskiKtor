package com.tynkovski.security.token

import com.tynkovski.data.entity.UserEntity

data class TokenClaim(
    val name: String,
    val value: String
) {
    companion object {
        fun create(user: UserEntity) = TokenClaim("userId", user.id)
    }
}
