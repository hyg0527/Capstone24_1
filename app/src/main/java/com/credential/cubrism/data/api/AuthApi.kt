package com.credential.cubrism.data.api

import com.credential.cubrism.data.dto.EmailVerifyDto
import com.credential.cubrism.data.dto.EmailVerifyRequestDto
import com.credential.cubrism.data.dto.SignUpDto
import com.credential.cubrism.data.dto.TokenDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/signup") // 회원가입
    fun signUp(@Body signUpDto: SignUpDto): Call<ResponseBody>

    @POST("/auth/signup/email/request") // 이메일 인증 번호 요청
    fun emailVerifyRequest(@Body emailVerifyRequestDto: EmailVerifyRequestDto): Call<ResponseBody>

    @POST("/auth/signup/email/verify") // 이메일 인증 번호 확인
    fun emailVerify(@Body emailVerifyDto: EmailVerifyDto): Call<ResponseBody>

    @POST("/auth/social/login/google") // 구글 로그인
    fun googleLogIn(@Query("serverAuthCode") serverAuthCode: String): Call<TokenDto>

    @POST("/auth/social/login/kakao") // 카카오 로그인
    fun kakaoLogIn(@Query("accessToken") accessToken: String): Call<TokenDto>
}