package com.credential.cubrism.data.api

import com.credential.cubrism.data.dto.EmailVerifyRequestDto
import com.credential.cubrism.data.dto.EmailVerifyDto
import com.credential.cubrism.data.dto.SignUpDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/signup") // 회원가입
    suspend fun signUp(@Body signUpDto: SignUpDto): Response<ResponseBody>

    @POST("/auth/signup/email/request") // 이메일 인증 번호 요청
    suspend fun emailVerifyRequest(@Body emailVerifyRequestDto: EmailVerifyRequestDto): Response<ResponseBody>

    @POST("/auth/signup/email/verify") // 이메일 인증 번호 확인
    suspend fun emailVerify(@Body emailVerifyDto: EmailVerifyDto): Response<ResponseBody>
}