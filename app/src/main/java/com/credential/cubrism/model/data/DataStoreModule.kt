package com.credential.cubrism.model.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.credential.cubrism.security.KeystoreEncryptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreModule(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "MyPrefs")

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    // Access Token 저장
    suspend fun saveAccessToken(accessToken: String) {
        val encryptedToken = KeystoreEncryptor.encrypt(accessToken)
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = encryptedToken
        }
    }

    // Access Token 불러오기
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[accessTokenKey]?.let { KeystoreEncryptor.decrypt(it)}
        }
    }

    // Refresh Token 저장
    suspend fun saveRefreshToken(refreshToken: String) {
        val encryptedToken = KeystoreEncryptor.encrypt(refreshToken)
        context.dataStore.edit { preferences ->
            preferences[refreshTokenKey] = encryptedToken
        }
    }

    // Refresh Token 불러오기
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[refreshTokenKey]?.let { KeystoreEncryptor.decrypt(it)}
        }
    }
}