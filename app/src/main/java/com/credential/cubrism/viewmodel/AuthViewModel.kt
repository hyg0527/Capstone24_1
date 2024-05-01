package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SocialTokenDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.dto.UserEditDto
import com.credential.cubrism.model.dto.UserInfoDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _signUp = MutableLiveData<ResultUtil<MessageDto>>()
    val signUp: LiveData<ResultUtil<MessageDto>> = _signUp

    private val _emailVerifyRequest = MutableLiveData<ResultUtil<MessageDto>>()
    val emailVerifyRequest: LiveData<ResultUtil<MessageDto>> = _emailVerifyRequest

    private val _emailVerify = MutableLiveData<ResultUtil<MessageDto>>()
    val emailVerify: LiveData<ResultUtil<MessageDto>> = _emailVerify

    private val _signIn = MutableLiveData<TokenDto>()
    val signIn: LiveData<TokenDto> = _signIn

    private val _googleLogIn = MutableLiveData<TokenDto>()
    val googleLogIn: LiveData<TokenDto> = _googleLogIn

    private val _kakaoLogIn = MutableLiveData<TokenDto>()
    val kakaoLogIn: LiveData<TokenDto> = _kakaoLogIn

    private val _logOut = MutableLiveData<MessageDto>()
    val logOut: LiveData<MessageDto> = _logOut

    private val _getUserInfo = MutableLiveData<UserInfoDto>()
    val getUserInfo: LiveData<UserInfoDto> = _getUserInfo

    private val _editUserInfo = MutableLiveData<MessageDto>()
    val editUserInfo: LiveData<MessageDto> = _editUserInfo

    private val _resetPassword = MutableLiveData<MessageDto>()
    val resetPassword: LiveData<MessageDto> = _resetPassword

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun signUp(email: String, password: String, nickname: String) {
        authRepository.signUp(email, password, nickname) { result ->
            _signUp.postValue(result)
        }
    }

    fun emailVerifyRequest(email: String) {
        authRepository.emailVerifyRequest(email) { result ->
            _emailVerifyRequest.postValue(result)
        }
    }

    fun emailVerify(email: String, code: String) {
        authRepository.emailVerify(email, code) { result ->
            _emailVerify.postValue(result)
        }
    }

    fun signIn(email: String, password: String) {
        authRepository.signIn(SignInDto(email, password)) { result ->
            handleResult(result, _signIn, _errorMessage)
        }
    }

    fun googleSignIn(socialTokenDto: SocialTokenDto) {
        authRepository.googleLogin(socialTokenDto) { result ->
            handleResult(result, _googleLogIn, _errorMessage)
        }
    }

    fun kakaoSignIn(socialTokenDto: SocialTokenDto) {
        authRepository.kakaoLogin(socialTokenDto) { result ->
            handleResult(result, _kakaoLogIn, _errorMessage)
        }
    }

    fun logOut() {
        authRepository.logOut { result ->
            handleResult(result, _logOut, _errorMessage)
        }
    }

    fun getUserInfo() {
        authRepository.getUserInfo { result ->
            handleResult(result, _getUserInfo, _errorMessage)
        }
    }

    fun editUserInfo(nickname: String, profileImage: String?) {
        authRepository.editUserInfo(UserEditDto(nickname, profileImage)) { result ->
            handleResult(result, _editUserInfo, _errorMessage)
        }
    }

    fun resetPassword(emailDto: EmailVerifyRequestDto) {
        authRepository.resetPassword(emailDto) { result ->
            handleResult(result, _resetPassword, _errorMessage)
        }
    }

    private fun <T> handleResult(result: ResultUtil<T>, successLiveData: MutableLiveData<T>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}