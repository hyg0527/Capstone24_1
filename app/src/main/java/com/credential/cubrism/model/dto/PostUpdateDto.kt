package com.credential.cubrism.model.dto

data class PostUpdateDto(
    val title: String,
    val content: String,
    val category: String,
    val images: List<String?>,
    val removedImages: List<String?>
)