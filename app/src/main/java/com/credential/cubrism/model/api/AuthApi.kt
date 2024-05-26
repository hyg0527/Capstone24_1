package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.EmailVerifyDto
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SignInSuccessDto
import com.credential.cubrism.model.dto.SignUpDto
import com.credential.cubrism.model.dto.SocialLogInDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.dto.UserEditDto
import com.credential.cubrism.model.dto.UserInfoDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    // 회원가입
    @POST("/auth/signup")
    fun signUp(@Body signUpDto: SignUpDto): Call<MessageDto>

    // 로그인
    @POST("/auth/signin")
    fun signIn(@Body signInDto: SignInDto): Call<SignInSuccessDto>

    // 구글 로그인
    @POST("/auth/signin/google")
    fun googleLogIn(@Body socialLogInDto: SocialLogInDto): Call<SignInSuccessDto>

    // 카카오 로그인
    @POST("/auth/signin/kakao")
    fun kakaoLogIn(@Body socialLogInDto: SocialLogInDto): Call<SignInSuccessDto>

    // 로그아웃
    @POST("/auth/logout")
    fun logOut(): Call<MessageDto>

    // 로그인 유저 정보
    @GET("/auth/users")
    fun getUserInfo(): Call<UserInfoDto>

    // 회원 정보 수정
    @PUT("/auth/users")
    fun editUserInfo(@Body userEditDto: UserEditDto): Call<UserInfoDto>

    // 회원 탈퇴
    @DELETE("/auth/users")
    fun withdrawal(): Call<MessageDto>

    // 이메일 인증 번호 요청
    @POST("/auth/signup/email/request")
    fun emailVerifyRequest(@Body emailVerifyRequestDto: EmailVerifyRequestDto): Call<MessageDto>

    // 이메일 인증 번호 확인
    @POST("/auth/signup/email/verify")
    fun emailVerify(@Body emailVerifyDto: EmailVerifyDto): Call<MessageDto>

    @POST("/auth/users/password")
    fun resetPassword(@Body emailVerifyRequestDto: EmailVerifyRequestDto): Call<MessageDto>

    // Access Token 재발급
    @POST("/auth/token/access")
    fun reissueAccessToken(@Header("AccessToken") accessToken: String, @Header("RefreshToken") refreshToken: String): Call<TokenDto>

    // Refresh Token 재발급
    @POST("/auth/token/refresh")
    fun reissueRefreshToken(@Header("Authorization") accessToken: String): Call<TokenDto>
}