package com.credential.cubrism.model.dto

data class StudyGroupInfoDto(
    val studyGroupId: Int,
    val groupName: String,
    val groupDescription: String,
    val groupAdmin: String,
    val adminProfileImage: String?,
    val currentMembers: Int,
    val maxMembers: Int,
    val tags: List<String>,
    val members: List<String>,
    val recruiting: Boolean
)
