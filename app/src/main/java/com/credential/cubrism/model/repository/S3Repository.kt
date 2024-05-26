package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.S3Api
import com.credential.cubrism.model.dto.PresignedUrlDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil
import okhttp3.RequestBody
import okhttp3.ResponseBody

class S3Repository {
    private val s3Api: S3Api = RetrofitClient.getRetrofitWithAuth()?.create(S3Api::class.java)!!
    private val s3ApiXml: S3Api = RetrofitClient.getRetrofitWithXml()?.create(S3Api::class.java)!!

    fun getPresignedUrl(presignedUrlRequestDto: List<PresignedUrlRequestDto>, callback: (ResultUtil<List<PresignedUrlDto>>) -> Unit) {
        handleResponse(s3Api.getPresignedUrl(presignedUrlRequestDto), callback)
    }

    fun uploadImage(presignedUrl: String, requestBody: RequestBody, callback: (ResultUtil<ResponseBody>) -> Unit) {
        handleResponse(s3ApiXml.uploadImage(presignedUrl, requestBody), callback)
    }
}