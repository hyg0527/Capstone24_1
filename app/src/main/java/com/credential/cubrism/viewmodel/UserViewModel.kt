package com.credential.cubrism.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.credential.cubrism.model.data.UserDataManager
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.model.utils.ResultUtil

class UserViewModelFactory(private val jwtTokenViewModel: JwtTokenViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(jwtTokenViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class UserViewModel(jwtTokenViewModel: JwtTokenViewModel) : ViewModel() {
    private val userRepository: UserRepository = UserRepository(jwtTokenViewModel)

    fun userInfo() {
        userRepository.userInfo { result ->
            when (result) {
                is ResultUtil.Success -> {
                    UserDataManager.setUserInfo(result.data)
                }
                is ResultUtil.Error -> {}
                is ResultUtil.NetworkError -> {}
            }
        }
    }
}