package com.credential.cubrism.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.credential.cubrism.model.repository.DataStoreRepository
import kotlinx.coroutines.launch

class DataStoreViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {
    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            dataStoreRepository.saveAccessToken(token)
        }
    }

    fun deleteAccessToken() {
        viewModelScope.launch {
            dataStoreRepository.deleteAccessToken()
        }
    }

    fun saveRefreshToken(token: String) {
        viewModelScope.launch {
            dataStoreRepository.saveRefreshToken(token)
        }
    }

    fun deleteRefreshToken() {
        viewModelScope.launch {
            dataStoreRepository.deleteRefreshToken()
        }
    }

    fun saveEmail(email: String) {
        viewModelScope.launch {
            dataStoreRepository.saveEmail(email)
        }
    }

    fun deleteEmail() {
        viewModelScope.launch {
            dataStoreRepository.deleteEmail()
        }
    }

    fun saveNickname(nickname: String) {
        viewModelScope.launch {
            dataStoreRepository.saveNickname(nickname)
        }
    }

    fun deleteNickname() {
        viewModelScope.launch {
            dataStoreRepository.deleteNickname()
        }
    }

    fun saveProfileImage(profileImage: String) {
        viewModelScope.launch {
            dataStoreRepository.saveProfileImage(profileImage)
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch {
            dataStoreRepository.deleteProfileImage()
        }
    }
}