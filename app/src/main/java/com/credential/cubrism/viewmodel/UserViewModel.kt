package com.credential.cubrism.viewmodel

import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.data.UserDataManager
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.model.utils.ResultUtil

class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    fun getUserInfo() {
        userRepository.userInfo { result ->
            when (result) {
                is ResultUtil.Success -> { UserDataManager.setUserInfo(result.data) }
                is ResultUtil.Error -> {}
                is ResultUtil.NetworkError -> {}
            }
        }
    }
}