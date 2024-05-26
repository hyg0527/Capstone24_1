package com.credential.cubrism.model.dto

data class StudyGroupGoalSubmitListDto(
    val userGoalId: Int,
    val nickname: String,
    val profileImageUrl: String,
    val content: String,
    val imageUrl: String,
    val submittedAt: String,
    val goalName: String
)
