package com.credential.cubrism.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.credential.cubrism.model.repository.JwtTokenRepository
import kotlinx.coroutines.launch

class JwtTokenViewModel(private val jwtTokenRepository: JwtTokenRepository) : ViewModel() {
    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            jwtTokenRepository.saveAccessToken(token)
        }
    }

    fun saveRefreshToken(token: String) {
        viewModelScope.launch {
            jwtTokenRepository.saveRefreshToken(token)
        }
    }

    fun deleteAccessToken() {
        viewModelScope.launch {
            jwtTokenRepository.deleteAccessToken()
        }
    }

    fun deleteRefreshToken() {
        viewModelScope.launch {
            jwtTokenRepository.deleteRefreshToken()
        }
    }
}