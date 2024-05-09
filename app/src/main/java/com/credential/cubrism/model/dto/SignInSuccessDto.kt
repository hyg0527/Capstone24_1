package com.credential.cubrism.model.dto

data class SignInSuccessDto(
    val user: UserInfoDto,
    val token: TokenDto
)
