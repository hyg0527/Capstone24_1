package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.FcmApi
import com.credential.cubrism.model.dto.FcmTokenDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FcmRepository {
    private val fcmApi: FcmApi = RetrofitClient.getRetrofitWithAuth()?.create(FcmApi::class.java)!!

    fun updateFcmToken(fcmTokenDto: FcmTokenDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        fcmApi.updateFcmToken(fcmTokenDto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(ResultUtil.Success(MessageDto("Success")))
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(it)) }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}