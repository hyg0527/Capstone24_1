package com.credential.cubrism.model.dto

data class PostMyListDto(
    val page: PageDto,
    val postList: List<PostMyList>
)

data class PostMyList(
    val postId: Int,
    val category: String,
    val nickname: String,
    val images: List<String>,
    val profileImage: String,
    val title: String,
    val content: String,
    val createdDate: String,
    val commentCount: Int
)