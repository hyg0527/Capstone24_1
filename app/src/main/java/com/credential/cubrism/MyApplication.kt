package com.credential.cubrism

import android.app.Application
import androidx.room.Room
import com.credential.cubrism.model.dao.NotiDao
import com.credential.cubrism.model.data.DataStoreModule
import com.credential.cubrism.model.database.NotiDatabase
import com.credential.cubrism.model.database.QualDatabase
import com.credential.cubrism.model.repository.DataStoreRepository
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var notiDatabase: NotiDatabase
    private lateinit var qualDatabase: QualDatabase

    companion object {
        private lateinit var myApplication: MyApplication
        fun getInstance(): MyApplication = myApplication
    }

    override fun onCreate() {
        super.onCreate()

        myApplication = this

        dataStoreModule = DataStoreModule(this)
        dataStoreRepository = DataStoreRepository(dataStoreModule)

        notiDatabase = Room.databaseBuilder(applicationContext, NotiDatabase::class.java, "Cubrism").build()
        qualDatabase = Room.databaseBuilder(applicationContext, QualDatabase::class.java, "Cubrism").build()

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    fun getDataStoreRepository(): DataStoreRepository = dataStoreRepository

    fun getNotiDao(): NotiDao = notiDatabase.notiDao()
}