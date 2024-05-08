package com.credential.cubrism.model.repository

import android.util.Log
import com.credential.cubrism.model.api.StudyGroupApi
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.StudyGroupCreateDto
import com.credential.cubrism.model.dto.StudyGroupInfoDto
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto
import com.credential.cubrism.model.dto.StudyGroupListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyGroupRepository {
    private val studyGroupApi: StudyGroupApi = RetrofitClient.getRetrofit()?.create(StudyGroupApi::class.java)!!
    private val studyGroupApiAuth: StudyGroupApi = RetrofitClient.getRetrofitWithAuth()?.create(StudyGroupApi::class.java)!!

    fun createStudyGroup(studyGroupCreateDto: StudyGroupCreateDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        studyGroupApiAuth.createStudyGroup(studyGroupCreateDto).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

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

    fun myStudyGroupList(callback: (ResultUtil<List<GroupList>>) -> Unit) {
        studyGroupApiAuth.getMyStudyGroupList().enqueue(object : Callback<List<GroupList>> {
            override fun onResponse(call: Call<List<GroupList>>, response: Response<List<GroupList>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<GroupList>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
                Log.d("테스트", "t : ${t.message}")
            }
        })
    }

    fun studyGroupInfo(groupId: Int, callback: (ResultUtil<StudyGroupInfoDto>) -> Unit) {
        studyGroupApi.getStudyGroupInfo(groupId).enqueue(object : Callback<StudyGroupInfoDto> {
            override fun onResponse(call: Call<StudyGroupInfoDto>, response: Response<StudyGroupInfoDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<StudyGroupInfoDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun requestJoin(groupId: Int, callback: (ResultUtil<MessageDto> ) -> Unit) {
        studyGroupApiAuth.requestJoin(groupId).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun joinRequestList(callback: (ResultUtil<List<StudyGroupJoinListDto>>) -> Unit) {
        studyGroupApiAuth.getJoinRequestList().enqueue(object : Callback<List<StudyGroupJoinListDto>> {
            override fun onResponse(call: Call<List<StudyGroupJoinListDto>>, response: Response<List<StudyGroupJoinListDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<StudyGroupJoinListDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun joinReceiveList(groupId: Int, callback: (ResultUtil<List<StudyGroupJoinReceiveListDto>>) -> Unit) {
        studyGroupApiAuth.getJoinReceiveList(groupId).enqueue(object : Callback<List<StudyGroupJoinReceiveListDto>> {
            override fun onResponse(call: Call<List<StudyGroupJoinReceiveListDto>>, response: Response<List<StudyGroupJoinReceiveListDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<StudyGroupJoinReceiveListDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}