package com.credential.cubrism.model.dto

data class ScheduleAddDto(
    val startDate: String,
    val endDate: String,
    val isAllDay: Boolean,
    val title: String,
    val content: String?,
)
