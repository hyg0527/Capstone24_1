package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.PresignedUrlDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.model.utils.ResultUtil

class S3ViewModel(private val s3Repository: S3Repository) : ViewModel() {
    private val _presignedUrl = MutableLiveData<ResultUtil<PresignedUrlDto>>()
    val presignedUrl: LiveData<ResultUtil<PresignedUrlDto>> = _presignedUrl

    fun getPresignedUrl(presignedUrlRequestDto: List<PresignedUrlRequestDto>) {
        s3Repository.getPresignedUrl(presignedUrlRequestDto) { result ->
            _presignedUrl.postValue(result)
        }
    }
}