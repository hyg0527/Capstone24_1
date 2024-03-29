package com.credential.cubrism.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.credential.cubrism.view.MyApplication
import kotlinx.coroutines.launch

class JwtTokenViewModel : ViewModel() {
    private val dataStore = MyApplication.getInstance().getDataStore()

    val accessToken = dataStore.getAccessToken().asLiveData()
    val refreshToken = dataStore.getRefreshToken().asLiveData()

    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            dataStore.saveAccessToken(token)
        }
    }

    fun saveRefreshToken(token: String) {
        viewModelScope.launch {
            dataStore.saveRefreshToken(token)
        }
    }
}