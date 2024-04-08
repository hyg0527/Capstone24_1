package com.credential.cubrism.model.dto

data class PresignedUrlDto(
    val fileName: String,
    val presignedUrl: String,
    val fileUrl: String
)
