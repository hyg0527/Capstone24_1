package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.PresignedUrlDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event
import okhttp3.RequestBody
import okhttp3.ResponseBody

class S3ViewModel(private val s3Repository: S3Repository) : ViewModel() {
    private val _presignedUrl = MutableLiveData<List<PresignedUrlDto>>()
    val presignedUrl: LiveData<List<PresignedUrlDto>> = _presignedUrl

    private val _uploadImage = MutableLiveData<ResponseBody>()
    val uploadImage: LiveData<ResponseBody> = _uploadImage

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getPresignedUrl(presignedUrlRequestDto: List<PresignedUrlRequestDto>) {
        s3Repository.getPresignedUrl(presignedUrlRequestDto) { result ->
            handleResult(result, _presignedUrl, _errorMessage)
        }
    }

    fun uploadImage(presignedUrl: String, requestBody: RequestBody) {
        s3Repository.uploadImage(presignedUrl, requestBody) { result ->
            handleResult(result, _uploadImage, _errorMessage)
        }
    }

    private fun <T> handleResult(result: ResultUtil<T>, successLiveData: MutableLiveData<T>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}