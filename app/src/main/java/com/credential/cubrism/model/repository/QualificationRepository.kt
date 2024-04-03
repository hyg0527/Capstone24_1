package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.QualificationApi
import com.credential.cubrism.model.dto.MajorFieldDto
import com.credential.cubrism.model.dto.MiddleFieldDto
import com.credential.cubrism.model.dto.QualificationDetailsDto
import com.credential.cubrism.model.dto.QualificationListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QualificationRepository {
    private val qualificationApi: QualificationApi = RetrofitClient.getRetrofit()?.create(QualificationApi::class.java)!!

    fun qualificationList(callback: (ResultUtil<List<QualificationListDto>>) -> Unit) {
        qualificationApi.getQualificationList().enqueue(object : Callback<List<QualificationListDto>> {
            override fun onResponse(call: Call<List<QualificationListDto>>, response: Response<List<QualificationListDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<QualificationListDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun majorFieldList(callback: (ResultUtil<List<MajorFieldDto>>) -> Unit) {
        qualificationApi.getMajorFieldList().enqueue(object : Callback<List<MajorFieldDto>> {
            override fun onResponse(call: Call<List<MajorFieldDto>>, response: Response<List<MajorFieldDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<MajorFieldDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun middleFieldList(field: String, callback: (ResultUtil<List<MiddleFieldDto>>) -> Unit) {
        qualificationApi.getMiddleFieldList(field).enqueue(object : Callback<List<MiddleFieldDto>> {
            override fun onResponse(call: Call<List<MiddleFieldDto>>, response: Response<List<MiddleFieldDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<MiddleFieldDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun qualificationDetails(code: String, callback: (ResultUtil<QualificationDetailsDto>) -> Unit) {
        qualificationApi.getQualificationDetails(code).enqueue(object : Callback<QualificationDetailsDto> {
            override fun onResponse(call: Call<QualificationDetailsDto>, response: Response<QualificationDetailsDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<QualificationDetailsDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}