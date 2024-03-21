package com.credential.cubrism.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.credential.cubrism.BuildConfig

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getRetrofit(): Retrofit? {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BuildConfig.SPRING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}