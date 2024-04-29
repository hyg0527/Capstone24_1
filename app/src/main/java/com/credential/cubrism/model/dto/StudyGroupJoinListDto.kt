package com.credential.cubrism.model.dto

data class StudyGroupJoinListDto(
    val groupName: String,
    val groupDescription: String,
    val tags: List<String>,
    val requestDate: String
)
