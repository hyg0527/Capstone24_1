package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.S3Api
import com.credential.cubrism.model.dto.PresignedUrlDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class S3Repository {
    private val s3Api: S3Api = RetrofitClient.getRetrofitWithAuth()?.create(S3Api::class.java)!!
    private val s3ApiXml: S3Api = RetrofitClient.getRetrofitWithXml()?.create(S3Api::class.java)!!

    fun getPresignedUrl(presignedUrlRequestDto: List<PresignedUrlRequestDto>, callback: (ResultUtil<PresignedUrlDto>) -> Unit) {
        s3Api.getPresignedUrl(presignedUrlRequestDto).enqueue(object : Callback<PresignedUrlDto> {
            override fun onResponse(call: Call<PresignedUrlDto>, response: Response<PresignedUrlDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(it))
                    }
                }
            }

            override fun onFailure(call: Call<PresignedUrlDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun uploadImage(presignedUrl: String, requestBody: RequestBody) {
        s3ApiXml.uploadImage(presignedUrl, requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 성공
                } else {
                    // 실패
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 실패
            }
        })
    }
}