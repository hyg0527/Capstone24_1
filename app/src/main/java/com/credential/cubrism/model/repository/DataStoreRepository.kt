package com.credential.cubrism.model.repository

import com.credential.cubrism.model.data.DataStoreModule
import kotlinx.coroutines.flow.Flow

class DataStoreRepository(private val dataStore: DataStoreModule) {
    suspend fun saveAccessToken(token: String) {
        dataStore.saveAccessToken(token)
    }

    suspend fun deleteAccessToken() {
        dataStore.deleteAccessToken()
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.getAccessToken()
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.saveRefreshToken(token)
    }

    suspend fun deleteRefreshToken() {
        dataStore.deleteRefreshToken()
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.getRefreshToken()
    }

    suspend fun saveFcmToken(token: String) {
        dataStore.saveFcmToken(token)
    }

    fun getFcmToken(): Flow<String?> {
        return dataStore.getFcmToken()
    }

    suspend fun deleteFcmToken() {
        dataStore.deleteFcmToken()
    }
}