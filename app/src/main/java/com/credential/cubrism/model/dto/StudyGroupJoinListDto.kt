package com.credential.cubrism.model.dto

data class StudyGroupJoinListDto(
    val memberId: String,
    val groupName: String,
    val groupDescription: String,
    val tags: List<String>,
    val groupAdmin: String,
    val groupAdminProfileImage: String?,
    val requestDate: String
)
