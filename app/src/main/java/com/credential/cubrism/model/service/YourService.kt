package com.credential.cubrism.model.service

import com.credential.cubrism.model.dto.FcmTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface YourService {
    @PUT("users/fcmtoken/{email}")
    fun updateFcmToken(@Path("email") email: String, @Body request: FcmTokenRequest): Call<Void>
}