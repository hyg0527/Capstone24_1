package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.dto.UserInfoDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    private val authApi: AuthApi = RetrofitClient.getRetrofitWithAuth()?.create(AuthApi::class.java)!!

    fun userInfo(callback: (ResultUtil<UserInfoDto>) -> Unit) {
        authApi.getUserInfo().enqueue(object : Callback<UserInfoDto> {
            override fun onResponse(call: Call<UserInfoDto>, response: Response<UserInfoDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).optString("message")))
                    }
                }
            }

            override fun onFailure(call: Call<UserInfoDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}