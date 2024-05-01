package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.ScheduleApi
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleRepository {
    private val scheduleApi: ScheduleApi = RetrofitClient.getRetrofitWithAuth()?.create(ScheduleApi::class.java)!!

    fun addSchedule(scheduleDto: ScheduleDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        scheduleApi.addSchedule(scheduleDto).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun deleteSchedule(scheduleId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        scheduleApi.deleteSchedule(scheduleId).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun updateSchedule(scheduleId: Int, scheduleDto: ScheduleDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        scheduleApi.updateSchedule(scheduleId, scheduleDto).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun getScheduleList(year: Int?, month: Int?, callback: (ResultUtil<List<ScheduleListDto>>) -> Unit) {
        scheduleApi.getScheduleList(year, month).enqueue(object : Callback<List<ScheduleListDto>> {
            override fun onResponse(call: Call<List<ScheduleListDto>>, response: Response<List<ScheduleListDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<ScheduleListDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}