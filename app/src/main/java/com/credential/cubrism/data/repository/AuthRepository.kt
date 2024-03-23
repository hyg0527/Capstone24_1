package com.credential.cubrism.data.repository

import com.credential.cubrism.data.api.AuthApi
import com.credential.cubrism.data.dto.SignInDto
import com.credential.cubrism.data.dto.TokenDto
import com.credential.cubrism.data.service.RetrofitClient
import com.credential.cubrism.data.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private val authApi: AuthApi = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)!!

    fun signIn(signInDto: SignInDto, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.signIn(signInDto).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Failure(JSONObject(it).getString("message")))
                    }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.Failure("네트워크 오류가 발생했습니다"))
            }
        })
    }

    fun googleLogin(code: String, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.googleLogIn(code).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Failure(JSONObject(it).getString("message")))
                    }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.Failure("네트워크 오류가 발생했습니다"))
            }
        })
    }

    fun kakaoLogin(accessToken: String, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.kakaoLogIn(accessToken).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Failure(JSONObject(it).getString("message")))
                    }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.Failure("네트워크 오류가 발생했습니다"))
            }
        })
    }
}