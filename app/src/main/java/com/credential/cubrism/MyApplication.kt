package com.credential.cubrism

import android.app.Application
import com.credential.cubrism.model.data.DataStoreModule
import com.credential.cubrism.model.repository.DataStoreRepository
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var dataStoreRepository: DataStoreRepository

    companion object {
        private lateinit var myApplication: MyApplication
        fun getInstance(): MyApplication = myApplication
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
        dataStoreModule = DataStoreModule(this)
        dataStoreRepository = DataStoreRepository(dataStoreModule)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    fun getDataStoreRepository(): DataStoreRepository = dataStoreRepository
}