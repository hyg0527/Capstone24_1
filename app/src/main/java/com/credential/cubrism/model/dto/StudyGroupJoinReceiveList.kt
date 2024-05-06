package com.credential.cubrism.model.dto

import java.util.UUID

data class StudyGroupJoinReceiveList(
    val memberId: UUID,
    val groupName: String,
    val userName: String,
    val userImage: String?,
    val requestDate: String
)