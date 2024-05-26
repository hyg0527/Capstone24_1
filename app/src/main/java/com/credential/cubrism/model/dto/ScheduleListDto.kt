package com.credential.cubrism.model.dto

import java.io.Serializable

data class ScheduleListDto(
    val scheduleId: Int,
    val startDate: String,
    val endDate: String,
    val title: String,
    val content: String,
    val allDay: Boolean
): Serializable