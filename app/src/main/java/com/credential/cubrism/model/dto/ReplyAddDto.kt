package com.credential.cubrism.model.dto

data class ReplyAddDto(
    val postId: Int,
    val commentId: Int,
    val content: String
)