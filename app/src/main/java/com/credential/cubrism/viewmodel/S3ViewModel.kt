package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.PresignedUrlDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class S3ViewModel(private val s3Repository: S3Repository) : ViewModel() {
    private val _presignedUrl = MutableLiveData<List<PresignedUrlDto>>()
    val presignedUrl: LiveData<List<PresignedUrlDto>> = _presignedUrl

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getPresignedUrl(presignedUrlRequestDto: List<PresignedUrlRequestDto>) {
        s3Repository.getPresignedUrl(presignedUrlRequestDto) { result ->
            when (result) {
                is ResultUtil.Success -> { _presignedUrl.postValue(result.data) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }
}