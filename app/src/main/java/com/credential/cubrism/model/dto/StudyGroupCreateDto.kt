package com.credential.cubrism.model.dto

data class StudyGroupCreateDto(
    val groupName: String,
    val groupDescription: String,
    val maxMembers: Int,
    val tags: List<String>,
)
