package com.credential.cubrism.model.dto

import java.time.LocalDateTime
import java.util.UUID

data class ChatResponse(
    val id: UUID,
    val userId: UUID,
    val username: String,
    val profileImgUrl: String,
    val createdAt: LocalDateTime,
    val content: String
)