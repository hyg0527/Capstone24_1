package com.credential.cubrism.model.dto

data class PostListDto(
    val page: PageDto,
    val postList: List<PostList>
)

data class PostList(
    val postId: Int,
    val category: String,
    val nickname: String?,
    val imageUrl: String?,
    val title: String,
    val content: String,
    val createdDate: String,
    val commentCount: Int
)