package com.credential.cubrism.model.dto

data class PageDto(
    val previousPage: Int?,
    val currentPage: Int,
    val nextPage: Int?
)
