package com.credential.cubrism.model.repository

import com.credential.cubrism.MyApplication
import kotlinx.coroutines.flow.Flow

class DataStoreRepository {
    private val dataStore = MyApplication.getInstance().getDataStore()

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

    suspend fun saveEmail(email: String) {
        dataStore.saveEmail(email)
    }

    fun getEmail(): Flow<String?> {
        return dataStore.getEmail()
    }

    suspend fun deleteEmail() {
        dataStore.deleteEmail()
    }

    suspend fun saveNickname(nickname: String) {
        dataStore.saveNickname(nickname)
    }

    fun getNickname(): Flow<String?> {
        return dataStore.getNickname()
    }

    suspend fun deleteNickname() {
        dataStore.deleteNickname()
    }

    suspend fun saveProfileImage(profileImage: String) {
        dataStore.saveProfileImage(profileImage)
    }

    fun getProfileImage(): Flow<String?> {
        return dataStore.getProfileImage()
    }

    suspend fun deleteProfileImage() {
        dataStore.deleteProfileImage()
    }
}