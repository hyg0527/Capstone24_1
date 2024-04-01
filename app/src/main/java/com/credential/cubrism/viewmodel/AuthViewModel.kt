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

    fun googleSignIn(serverAuthCode: String) {
        authRepository.googleLogin(serverAuthCode) { result ->
            _googleSignIn.postValue(result)
        }
    }

    fun kakaoSignIn(accessToken: String) {
        authRepository.kakaoLogin(accessToken) { result ->
            _kakaoSignIn.postValue(result)
        }
    }
}