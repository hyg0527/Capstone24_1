package com.credential.cubrism.model.dto

import java.util.UUID

data class ChatResponseDto(
    val id: UUID,
    val email: String?,
    val username: String?,
    val profileImgUrl: String?,
    val createdAt: String,
    val content: String
)