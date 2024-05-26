package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.ScheduleApi
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil

class ScheduleRepository {
    private val scheduleApi: ScheduleApi = RetrofitClient.getRetrofitWithAuth()?.create(ScheduleApi::class.java)!!

    fun addSchedule(scheduleDto: ScheduleDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(scheduleApi.addSchedule(scheduleDto), callback)
    }

    fun deleteSchedule(scheduleId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(scheduleApi.deleteSchedule(scheduleId), callback)
    }

    fun updateSchedule(scheduleId: Int, scheduleDto: ScheduleDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(scheduleApi.updateSchedule(scheduleId, scheduleDto), callback)
    }

    fun getScheduleList(year: Int?, month: Int?, callback: (ResultUtil<List<ScheduleListDto>>) -> Unit) {
        handleResponse(scheduleApi.getScheduleList(year, month), callback)
    }
}