package com.credential.cubrism.data.utils

sealed class ResultUtil<out T> {
    data class Success<out T>(val data: T) : ResultUtil<T>()
    data class Error(val error: String) : ResultUtil<Nothing>()
    data class NetworkError(val networkError: String = "네트워크 오류가 발생했습니다") : ResultUtil<Nothing>()
}