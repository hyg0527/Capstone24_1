package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.dto.EmailVerifyDto
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SignUpDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private val authApi: AuthApi = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)!!

    fun signUp(email: String, password: String, nickname: String, callback: (ResultUtil<ResponseBody>) -> Unit) {
        authApi.signUp(SignUpDto(email, password, nickname)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun emailVerifyRequest(email: String, callback: (ResultUtil<ResponseBody>) -> Unit) {
        authApi.emailVerifyRequest(EmailVerifyRequestDto(email)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun emailVerify(email: String, code: String, callback: (ResultUtil<ResponseBody>) -> Unit) {
        authApi.emailVerify(EmailVerifyDto(email, code)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun signIn(signInDto: SignInDto, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.signIn(signInDto).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun googleLogin(code: String, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.googleLogIn(code).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun kakaoLogin(accessToken: String, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.kakaoLogIn(accessToken).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}