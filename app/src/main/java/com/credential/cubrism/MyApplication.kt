package com.credential.cubrism

import android.app.Application
import com.credential.cubrism.model.data.DataStoreModule
import com.credential.cubrism.model.data.UserDataManager
import com.credential.cubrism.model.repository.JwtTokenRepository
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    private lateinit var dataStore: DataStoreModule
    private lateinit var userDataManager: UserDataManager
    private lateinit var jwtTokenRepository: JwtTokenRepository

    companion object {
        private lateinit var myApplication: MyApplication
        fun getInstance(): MyApplication = myApplication
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
        dataStore = DataStoreModule(this)
        userDataManager = UserDataManager
        jwtTokenRepository = JwtTokenRepository()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    fun getDataStore(): DataStoreModule = dataStore

    fun getUserDataManager(): UserDataManager = userDataManager

    fun getJwtTokenRepository(): JwtTokenRepository = jwtTokenRepository
}