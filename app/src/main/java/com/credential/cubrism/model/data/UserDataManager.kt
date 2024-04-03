package com.credential.cubrism.model.data

import com.credential.cubrism.model.dto.UserInfoDto

object UserDataManager {
    private var userInfo: UserInfoDto? = null

    fun setUserInfo(userInfoDto: UserInfoDto) {
        userInfo = userInfoDto
    }

    fun getUserInfo(): UserInfoDto? {
        return userInfo
    }
}