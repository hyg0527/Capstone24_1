package com.credential.cubrism.model.dto

data class SignInDto(
    val email: String,
    val password: String,
    val fcmToken: String?
)