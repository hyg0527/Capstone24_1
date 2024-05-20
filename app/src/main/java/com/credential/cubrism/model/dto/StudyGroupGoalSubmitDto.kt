package com.credential.cubrism.model.dto

data class StudyGroupGoalSubmitDto(
    val goalId: Int,
    val groupId: Int,
    val content: String,
    val imageUrl: String
)