package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SocialTokenDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.dto.UserEditDto
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

    private val _signIn = MutableLiveData<ResultUtil<TokenDto>>()
    val signIn: LiveData<ResultUtil<TokenDto>> = _signIn

    private val _googleSignIn = MutableLiveData<ResultUtil<TokenDto>>()
    val googleSignIn: LiveData<ResultUtil<TokenDto>> = _googleSignIn

    private val _kakaoSignIn = MutableLiveData<ResultUtil<TokenDto>>()
    val kakaoSignIn: LiveData<ResultUtil<TokenDto>> = _kakaoSignIn

    private val _logOut = MutableLiveData<ResultUtil<MessageDto>>()
    val logOut: LiveData<ResultUtil<MessageDto>> = _logOut

    private val _editUserInfo = MutableLiveData<MessageDto>()
    val editUserInfo: LiveData<MessageDto> = _editUserInfo

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
            _signIn.postValue(result)
        }
    }

    fun googleSignIn(socialTokenDto: SocialTokenDto) {
        authRepository.googleLogin(socialTokenDto) { result ->
            _googleSignIn.postValue(result)
        }
    }

    fun kakaoSignIn(socialTokenDto: SocialTokenDto) {
        authRepository.kakaoLogin(socialTokenDto) { result ->
            _kakaoSignIn.postValue(result)
        }
    }

    fun logOut() {
        authRepository.logOut { result ->
            _logOut.postValue(result)
        }
    }

    fun editUserInfo(nickname: String, profileImage: String?) {
        authRepository.editUserInfo(UserEditDto(nickname, profileImage)) { result ->
            handleResult(result, _editUserInfo, _errorMessage)
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