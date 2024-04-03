package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.TokenDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _signUpResult = MutableLiveData<ResultUtil<MessageDto>>()
    val signUpResult: LiveData<ResultUtil<MessageDto>> = _signUpResult

    private val _emailVerifyRequestResult = MutableLiveData<ResultUtil<MessageDto>>()
    val emailVerifyRequestResult: LiveData<ResultUtil<MessageDto>> = _emailVerifyRequestResult

    private val _emailVerifyResult = MutableLiveData<ResultUtil<MessageDto>>()
    val emailVerifyResult: LiveData<ResultUtil<MessageDto>> = _emailVerifyResult

    private val _signInResult = MutableLiveData<ResultUtil<TokenDto>>()
    val signInResult: LiveData<ResultUtil<TokenDto>> = _signInResult

    private val _googleSignInResult = MutableLiveData<ResultUtil<TokenDto>>()
    val googleSignInResult: LiveData<ResultUtil<TokenDto>> = _googleSignInResult

    private val _kakaoSignInResult = MutableLiveData<ResultUtil<TokenDto>>()
    val kakaoSignInResult: LiveData<ResultUtil<TokenDto>> = _kakaoSignInResult

    fun signUp(email: String, password: String, nickname: String) {
        authRepository.signUp(email, password, nickname) { result ->
            _signUpResult.postValue(result)
        }
    }

    fun emailVerifyRequest(email: String) {
        authRepository.emailVerifyRequest(email) { result ->
            _emailVerifyRequestResult.postValue(result)
        }
    }

    fun emailVerify(email: String, code: String) {
        authRepository.emailVerify(email, code) { result ->
            _emailVerifyResult.postValue(result)
        }
    }

    fun signIn(email: String, password: String) {
        authRepository.signIn(SignInDto(email, password)) { result ->
            _signInResult.postValue(result)
        }
    }

    fun googleSignIn(serverAuthCode: String) {
        authRepository.googleLogin(serverAuthCode) { result ->
            _googleSignInResult.postValue(result)
        }
    }

    fun kakaoSignIn(accessToken: String) {
        authRepository.kakaoLogin(accessToken) { result ->
            _kakaoSignInResult.postValue(result)
        }
    }
}