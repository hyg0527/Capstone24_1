package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.EmailVerifyDto
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SignUpDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.dto.UserInfoDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/signup") // 회원가입
    fun signUp(@Body signUpDto: SignUpDto): Call<MessageDto>

    @POST("/auth/signup/email/request") // 이메일 인증 번호 요청
    fun emailVerifyRequest(@Body emailVerifyRequestDto: EmailVerifyRequestDto): Call<MessageDto>

    @POST("/auth/signup/email/verify") // 이메일 인증 번호 확인
    fun emailVerify(@Body emailVerifyDto: EmailVerifyDto): Call<MessageDto>

    @POST("/auth/signin") // 로그인
    fun signIn(@Body signInDto: SignInDto): Call<TokenDto>

    @POST("/auth/social/login/google") // 구글 로그인
    fun googleLogIn(@Query("serverAuthCode") serverAuthCode: String): Call<TokenDto>

    @POST("/auth/social/login/kakao") // 카카오 로그인
    fun kakaoLogIn(@Query("accessToken") accessToken: String): Call<TokenDto>

    @GET("/auth/info") // 유저 정보
    fun getUserInfo(): Call<UserInfoDto>

    @POST("/auth/reissue-access-token") // Access Token 재발급
    fun reissueAccessToken(@Header("AccessToken") accessToken: String, @Header("RefreshToken") refreshToken: String): Call<TokenDto>

    @POST("/auth/reissue-refresh-token") // Refresh Token 재발급
    fun reissueRefreshToken(): Call<TokenDto>

    @DELETE("/auth/logout") // 로그아웃
    fun logOut(): Call<MessageDto>
}