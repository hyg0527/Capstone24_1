package com.credential.cubrism.model.dto

data class StudyGroupListDto(
    val page: PageDto,
    val studyGroupList: List<GroupList>
)

data class GroupList(
    val studyGroupId: Int,
    val groupName: String,
    val groupDescription: String,
    val currentMembers: Int,
    val maxMembers: Int,
    val tags: List<String>,
    val recruiting: Boolean
)