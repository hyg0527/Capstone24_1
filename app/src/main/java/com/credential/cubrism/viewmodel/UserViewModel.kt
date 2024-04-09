package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.UserInfoDto
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userInfo = MutableLiveData<UserInfoDto>()
    val userInfo: LiveData<UserInfoDto> = _userInfo

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getUserInfo() {
        userRepository.userInfo { result ->
            when (result) {
                is ResultUtil.Success -> { _userInfo.postValue(result.data) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }
}