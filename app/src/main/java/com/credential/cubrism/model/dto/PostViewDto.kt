package com.credential.cubrism.model.dto

data class PostViewDto(
    val postId: Int,
    val boardName: String,
    val category: String,
    val nickname: String?,
    val profileImageUrl: String?,
    val email: String?,
    val title: String,
    val content: String,
    val createdDate: String,
    val images: List<String>,
    val comments: List<Comments>,
    val aiComment: String?
)

data class Comments(
    val commentId: Int,
    val replyTo: Int?,
    val replyToNickname: String?,
    val nickname: String?,
    val email: String?,
    val content: String,
    val createdDate: String,
    val profileImageUrl: String?,
    val isUpdated: Boolean
)