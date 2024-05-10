package com.credential.cubrism.model.dto

data class SocialLogInDto(
    val token: String,
    val fcmToken: String?
)
