package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.ChatApi
import com.credential.cubrism.model.dto.ChatResponse
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRepository {
    private val chatApi: ChatApi = RetrofitClient.getRetrofit()?.create(ChatApi::class.java)!!
    private val chatApiAuth: ChatApi = RetrofitClient.getRetrofitWithAuth()?.create(ChatApi::class.java)!!


    fun getChattingList(studygroupId: Long, callback: (ResultUtil<List<ChatResponse>>) -> Unit) {
        chatApiAuth.getChattingList(studygroupId).enqueue(object : Callback<List<ChatResponse>> {
            override fun onResponse(call: Call<List<ChatResponse>>, response: Response<List<ChatResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<ChatResponse>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}