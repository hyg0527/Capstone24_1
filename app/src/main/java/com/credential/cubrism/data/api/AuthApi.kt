package com.credential.cubrism.data.api

import com.credential.cubrism.data.dto.EmailVerifyRequestDto
import com.credential.cubrism.data.dto.EmailVerifyDto
import com.credential.cubrism.data.dto.SignUpDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/signup") // 회원가입
    fun signUp(@Body signUpDto: SignUpDto): Call<ResponseBody>

    @POST("/auth/signup/email/request") // 이메일 인증 번호 요청
    fun emailVerifyRequest(@Body emailVerifyRequestDto: EmailVerifyRequestDto): Call<ResponseBody>

    @POST("/auth/signup/email/verify") // 이메일 인증 번호 확인
    fun emailVerify(@Body emailVerifyDto: EmailVerifyDto): Call<ResponseBody>
}