package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {
    // 일정 추가
    @POST("/schedule")
    fun addSchedule(@Body scheduleDto: ScheduleDto): Call<MessageDto>

    // 일정 삭제
    @DELETE("/schedule/{scheduleId}")
    fun deleteSchedule(@Path("scheduleId") scheduleId: Int): Call<MessageDto>

    // 일정 수정
    @PUT("/schedule/{scheduleId}")
    fun updateSchedule(@Path("scheduleId") scheduleId: Int, @Body scheduleDto: ScheduleDto): Call<MessageDto>

    // 일정 목록
    @GET("/schedules")
    fun getScheduleList(
        @Query("year") year: Int?,
        @Query("month") month: Int?
    ): Call<List<ScheduleListDto>>
}