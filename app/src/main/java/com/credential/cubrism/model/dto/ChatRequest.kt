package com.credential.cubrism.model.dto

import java.util.UUID

data class ChatRequest(
    val userId: UUID,
    val content: String
)