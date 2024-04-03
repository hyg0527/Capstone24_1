package com.credential.cubrism.model.dto

data class UserInfoDto(
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val provider: String?
)
