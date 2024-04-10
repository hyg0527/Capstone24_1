package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.UserInfoDto
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.model.utils.ResultUtil

class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userInfo = MutableLiveData<UserInfoDto?>()
    val userInfo: LiveData<UserInfoDto?> = _userInfo

    fun getUserInfo() {
        userRepository.userInfo { result ->
            when (result) {
                is ResultUtil.Success -> { _userInfo.postValue(result.data) }
                is ResultUtil.Error -> { _userInfo.postValue(null) }
                is ResultUtil.NetworkError -> {}
            }
        }
    }
}