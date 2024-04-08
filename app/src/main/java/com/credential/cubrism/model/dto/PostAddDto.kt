package com.credential.cubrism.model.dto

data class PostAddDto(
    val title: String,
    val content: String,
    val boardId: Int,
    val category: String,
    val images: List<String?>
)