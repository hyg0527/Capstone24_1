package com.credential.cubrism.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.FcmTokenDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.repository.FcmRepository
import com.credential.cubrism.model.utils.ResultUtil

class FcmViewModel(private val fcmRepository: FcmRepository) : ViewModel() {
    private val _updateFcmToken = MutableLiveData<MessageDto>()

    fun updateFcmToken(fcmTokenDto: FcmTokenDto) {
        fcmRepository.updateFcmToken(fcmTokenDto) { result ->
            when (result) {
                is ResultUtil.Success -> _updateFcmToken.postValue(result.data)
                is ResultUtil.Error -> {}
                is ResultUtil.NetworkError -> {}
            }
        }
    }
}