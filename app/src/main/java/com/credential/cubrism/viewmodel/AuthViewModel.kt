package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.data.dto.SignInDto
import com.credential.cubrism.data.dto.TokenDto
import com.credential.cubrism.data.repository.AuthRepository
import com.credential.cubrism.data.utils.ResultUtil

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _signInResult = MutableLiveData<ResultUtil<TokenDto>>()
    val signInResult: LiveData<ResultUtil<TokenDto>>
        get() = _signInResult

    private val _googleSignInResult = MutableLiveData<ResultUtil<TokenDto>>()
    val googleSignInResult: LiveData<ResultUtil<TokenDto>>
        get() = _googleSignInResult

    private val _kakaoSignInResult = MutableLiveData<ResultUtil<TokenDto>>()
    val kakaoSignInResult: LiveData<ResultUtil<TokenDto>>
        get() = _kakaoSignInResult

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