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
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil

class AuthRepository {
    private val authApi: AuthApi = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)!!
    private val authApiAuth: AuthApi = RetrofitClient.getRetrofitWithAuth()?.create(AuthApi::class.java)!!

    fun signUp(email: String, password: String, nickname: String, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(authApi.signUp(SignUpDto(email, password, nickname)), callback)
    }

    fun emailVerifyRequest(email: String, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(authApi.emailVerifyRequest(EmailVerifyRequestDto(email)), callback)
    }

    fun emailVerify(email: String, code: String, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(authApi.emailVerify(EmailVerifyDto(email, code)), callback)
    }

    fun signIn(signInDto: SignInDto, callback: (ResultUtil<SignInSuccessDto>) -> Unit) {
        handleResponse(authApi.signIn(signInDto), callback)
    }

    fun googleLogin(socialLogInDto: SocialLogInDto, callback: (ResultUtil<SignInSuccessDto>) -> Unit) {
        handleResponse(authApi.googleLogIn(socialLogInDto), callback)
    }

    fun kakaoLogin(socialLogInDto: SocialLogInDto, callback: (ResultUtil<SignInSuccessDto>) -> Unit) {
        handleResponse(authApi.kakaoLogIn(socialLogInDto), callback)
    }

    fun logOut(callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(authApiAuth.logOut(), callback)
    }

    fun getUserInfo(callback: (ResultUtil<UserInfoDto>) -> Unit) {
        handleResponse(authApiAuth.getUserInfo(), callback)
    }

    fun editUserInfo(userEditDto: UserEditDto, callback: (ResultUtil<UserInfoDto>) -> Unit) {
        handleResponse(authApiAuth.editUserInfo(userEditDto), callback)
    }

    fun withdrawal(callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(authApiAuth.withdrawal(), callback)
    }

    fun resetPassword(emailDto: EmailVerifyRequestDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(authApi.resetPassword(emailDto), callback)
    }
}