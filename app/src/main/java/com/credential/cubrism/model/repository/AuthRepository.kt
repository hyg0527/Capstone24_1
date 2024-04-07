package com.credential.cubrism.model.repository

import android.util.Log
import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.dto.EmailVerifyDto
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SignUpDto
import com.credential.cubrism.model.dto.SocialTokenDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.dto.UserEditDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private val authApi: AuthApi = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)!!

    fun signUp(email: String, password: String, nickname: String, callback: (ResultUtil<MessageDto>) -> Unit) {
        authApi.signUp(SignUpDto(email, password, nickname)).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun emailVerifyRequest(email: String, callback: (ResultUtil<MessageDto>) -> Unit) {
        authApi.emailVerifyRequest(EmailVerifyRequestDto(email)).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun emailVerify(email: String, code: String, callback: (ResultUtil<MessageDto>) -> Unit) {
        authApi.emailVerify(EmailVerifyDto(email, code)).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
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
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun googleLogin(socialTokenDto: SocialTokenDto, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.googleLogIn(socialTokenDto).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun kakaoLogin(socialTokenDto: SocialTokenDto, callback: (ResultUtil<TokenDto>) -> Unit) {
        authApi.kakaoLogIn(socialTokenDto).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun editUserInfo(userEditDto: UserEditDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        authApi.editUserInfo(userEditDto).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}