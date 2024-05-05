package com.credential.cubrism.model.dto

import java.util.UUID

data class ChatRequestDto(
    val userId: UUID,
    val content: String
)