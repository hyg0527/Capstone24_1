package com.credential.cubrism.model.dto

data class StudyGroupEnterDto(
    val groupName: String,
    val members: List<MembersDto>,
    val day: DDayDto
)

data class MembersDto(
    val nickname: String,
    val email: String,
    val profileImage: String?,
    val admin: Boolean,
    val userGoal: UserGoalsDto
)

data class UserGoalsDto(
    val goals: List<GoalsDto>,
    val completionPercentage: Double?
)

data class GoalsDto(
    val index: Int?,
    val goalId: Int,
    val goalName: String,
    val completed: Boolean?
)

data class DDayDto(
    val groupId: Int,
    val title: String?,
    val day: String?
)