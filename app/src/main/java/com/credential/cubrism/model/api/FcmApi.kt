package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.FcmTokenDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface FcmApi {
    @PUT("/fcm")
    fun updateFcmToken(@Body request: FcmTokenDto): Call<Void>
}