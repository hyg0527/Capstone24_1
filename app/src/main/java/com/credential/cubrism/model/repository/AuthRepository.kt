package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.dto.EmailVerifyDto
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SignInSuccessDto
import com.credential.cubrism.model.dto.SignUpDto
import com.credential.cubrism.model.dto.SocialLogInDto
import com.credential.cubrism.model.dto.UserEditDto
import com.credential.cubrism.model.dto.UserInfoDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private val authApi: AuthApi = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)!!
    private val authApiAuth: AuthApi = RetrofitClient.getRetrofitWithAuth()?.create(AuthApi::class.java)!!

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

    fun signIn(signInDto: SignInDto, callback: (ResultUtil<SignInSuccessDto>) -> Unit) {
        authApi.signIn(signInDto).enqueue(object : Callback<SignInSuccessDto> {
            override fun onResponse(call: Call<SignInSuccessDto>, response: Response<SignInSuccessDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<SignInSuccessDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun googleLogin(socialLogInDto: SocialLogInDto, callback: (ResultUtil<SignInSuccessDto>) -> Unit) {
        authApi.googleLogIn(socialLogInDto).enqueue(object : Callback<SignInSuccessDto> {
            override fun onResponse(call: Call<SignInSuccessDto>, response: Response<SignInSuccessDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<SignInSuccessDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun kakaoLogin(socialLogInDto: SocialLogInDto, callback: (ResultUtil<SignInSuccessDto>) -> Unit) {
        authApi.kakaoLogIn(socialLogInDto).enqueue(object : Callback<SignInSuccessDto> {
            override fun onResponse(call: Call<SignInSuccessDto>, response: Response<SignInSuccessDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<SignInSuccessDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun logOut(callback: (ResultUtil<MessageDto>) -> Unit) {
        authApiAuth.logOut().enqueue(object : Callback<MessageDto> {
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

    fun getUserInfo(callback: (ResultUtil<UserInfoDto>) -> Unit) {
        authApiAuth.getUserInfo().enqueue(object : Callback<UserInfoDto> {
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

    fun editUserInfo(userEditDto: UserEditDto, callback: (ResultUtil<UserInfoDto>) -> Unit) {
        authApiAuth.editUserInfo(userEditDto).enqueue(object : Callback<UserInfoDto> {
            override fun onResponse(call: Call<UserInfoDto>, response: Response<UserInfoDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<UserInfoDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun withdrawal(callback: (ResultUtil<MessageDto>) -> Unit) {
        authApiAuth.withdrawal().enqueue(object : Callback<MessageDto> {
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

    fun resetPassword(emailDto: EmailVerifyRequestDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        authApi.resetPassword(emailDto).enqueue(object : Callback<MessageDto> {
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