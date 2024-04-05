package com.credential.cubrism.model.repository

import com.credential.cubrism.MyApplication
import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.dto.UserInfoDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JwtTokenRepository {
    private val dataStore = MyApplication.getInstance().getDataStore()
    private val authApi = RetrofitClient.getRetrofitWithAuth(this)?.create(AuthApi::class.java)!!

    suspend fun saveAccessToken(token: String) {
        dataStore.saveAccessToken(token)
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.saveRefreshToken(token)
    }

    suspend fun deleteAccessToken() {
        dataStore.deleteAccessToken()
    }

    suspend fun deleteRefreshToken() {
        dataStore.deleteRefreshToken()
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.getAccessToken()
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.getRefreshToken()
    }

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