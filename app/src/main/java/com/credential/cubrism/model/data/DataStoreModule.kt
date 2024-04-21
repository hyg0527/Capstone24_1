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

    private val emailKey = stringPreferencesKey("email")
    private val nicknameKey = stringPreferencesKey("nickname")
    private val profileImageKey = stringPreferencesKey("profile_image")

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

    // FCM Token 삭제
    suspend fun deleteFcmToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(fcmTokenKey)
        }
    }

    // 이메일 저장
    suspend fun saveEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[emailKey] = email
        }
    }

    // 이메일 불러오기
    fun getEmail(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[emailKey]
        }
    }

    // 이메일 삭제
    suspend fun deleteEmail() {
        context.dataStore.edit { preferences ->
            preferences.remove(emailKey)
        }
    }

    // 닉네임 저장
    suspend fun saveNickname(nickname: String) {
        context.dataStore.edit { preferences ->
            preferences[nicknameKey] = nickname
        }
    }

    // 닉네임 불러오기
    fun getNickname(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[nicknameKey]
        }
    }

    // 닉네임 삭제
    suspend fun deleteNickname() {
        context.dataStore.edit { preferences ->
            preferences.remove(nicknameKey)
        }
    }

    // 프로필 이미지 저장
    suspend fun saveProfileImage(profileImage: String) {
        context.dataStore.edit { preferences ->
            preferences[profileImageKey] = profileImage
        }
    }

    // 프로필 이미지 불러오기
    fun getProfileImage(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[profileImageKey]
        }
    }

    // 프로필 이미지 삭제
    suspend fun deleteProfileImage() {
        context.dataStore.edit { preferences ->
            preferences.remove(profileImageKey)
        }
    }
}