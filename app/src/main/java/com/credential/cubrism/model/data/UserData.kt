package com.credential.cubrism.model.data

object UserData {
    private var isLogin: Boolean = false
    private var email: String? = null
    private var nickname: String? = null
    private var profileImage: String? = null

    fun setLoginStatus(isLoggedIn: Boolean) {
        isLogin = isLoggedIn
    }

    fun getLoginStatus(): Boolean = isLogin

    fun setUserData(email: String, nickname: String, profileImage: String?) {
        this.email = email
        this.nickname = nickname
        this.profileImage = profileImage
    }

    fun deleteUserData() {
        email = null
        nickname = null
        profileImage = null
    }

    fun getEmail(): String? = email

    fun getNickname(): String? = nickname

    fun getProfileImage(): String? = profileImage
}