package com.credential.cubrism.data.utils

sealed class ResultUtil<out T> {
    data class Success<out T>(val data: T) : ResultUtil<T>()
    data class Failure(val error: String) : ResultUtil<Nothing>()
}