package com.credential.cubrism.model.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreModule(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "MyPrefs")

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    private val fcmTokenKey = stringPreferencesKey("fcm_token")

    // Access Token 저장
    suspend fun saveAccessToken(accessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
        }
    }

    // Access Token 불러오기
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[accessTokenKey]
        }
    }

    // Access Token 삭제
    suspend fun deleteAccessToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
        }
    }

    // Refresh Token 저장
    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[refreshTokenKey] = refreshToken
        }
    }

    // Refresh Token 불러오기
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[refreshTokenKey]
        }
    }

    // Refresh Token 삭제
    suspend fun deleteRefreshToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(refreshTokenKey)
        }
    }

    // FCM Token 저장
    suspend fun saveFcmToken(fcmToken: String) {
        context.dataStore.edit { preferences ->
            preferences[fcmTokenKey] = fcmToken
        }
    }

    // FCM Token 불러오기
    fun getFcmToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[fcmTokenKey]
        }
    }
}