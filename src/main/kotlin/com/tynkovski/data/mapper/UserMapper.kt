package com.tynkovski.data.mapper

import com.tynkovski.data.entity.UserEntity
import com.tynkovski.model.response.UserResponse

fun userMapper(userEntity: UserEntity): UserResponse = UserResponse(
    userEntity.id,
    userEntity.login,
    userEntity.name,
    userEntity.createdAt.value
)