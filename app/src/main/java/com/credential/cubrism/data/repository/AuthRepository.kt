package com.credential.cubrism.data.repository

import com.credential.cubrism.data.api.AuthApi
import com.credential.cubrism.data.dto.EmailVerifyDto
import com.credential.cubrism.data.dto.EmailVerifyRequestDto
import com.credential.cubrism.data.dto.SignInDto
import com.credential.cubrism.data.dto.SignUpDto
import com.credential.cubrism.data.dto.TokenDto
import com.credential.cubrism.data.service.RetrofitClient
import com.credential.cubrism.data.utils.ResultUtil
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
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).getString("message")))
                    }
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
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).getString("message")))
                    }
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
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).getString("message")))
                    }
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
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).getString("message")))
                    }
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
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).getString("message")))
                    }
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
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).getString("message")))
                    }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}