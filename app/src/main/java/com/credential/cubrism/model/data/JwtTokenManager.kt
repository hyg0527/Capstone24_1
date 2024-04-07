package com.credential.cubrism.model.data

import com.credential.cubrism.MyApplication
import kotlinx.coroutines.flow.Flow

class TokenManager {
    private val dataStore = MyApplication.getInstance().getDataStore()

    suspend fun saveAccessToken(token: String) {
        dataStore.saveAccessToken(token)
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.saveRefreshToken(token)
    }

    suspend fun deleteAccessToken() {
        dataStore.deleteAccessToken()
    }

    suspend fun deleteRefreshToken() {
        dataStore.deleteRefreshToken()
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.getAccessToken()
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.getRefreshToken()
    }
}