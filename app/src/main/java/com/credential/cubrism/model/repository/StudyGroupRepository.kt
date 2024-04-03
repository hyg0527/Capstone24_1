package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.StudyGroupApi
import com.credential.cubrism.model.dto.StudyGroupListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyGroupRepository {
    private val studyGroupApi: StudyGroupApi = RetrofitClient.getRetrofit()?.create(StudyGroupApi::class.java)!!

    fun studyGroupList(page: Int, limit: Int, recruiting: Boolean, callback: (ResultUtil<StudyGroupListDto>) -> Unit) {
        studyGroupApi.getStudyGroupList(page, limit, recruiting).enqueue(object : Callback<StudyGroupListDto> {
            override fun onResponse(call: Call<StudyGroupListDto>, response: Response<StudyGroupListDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<StudyGroupListDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}