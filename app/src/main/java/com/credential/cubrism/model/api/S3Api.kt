package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.PresignedUrlDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface S3Api {
    // Presigned URL 요청
    @POST("/s3/pre-signed-url")
    fun getPresignedUrl(@Body presignedUrlRequestDto: List<PresignedUrlRequestDto>): Call<List<PresignedUrlDto>>

    // 이미지 업로드
    @PUT
    fun uploadImage(@Url presignedUrl: String, @Body image: RequestBody): Call<ResponseBody>
}