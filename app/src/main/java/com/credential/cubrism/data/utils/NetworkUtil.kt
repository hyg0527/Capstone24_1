package com.credential.cubrism.data.utils

import retrofit2.Response

class NetworkUtil {
    companion object {
        suspend fun <T> executeNetworkCall(
            call: suspend () -> Response<T>?,
            onSuccess: (response: Response<T>) -> Unit,
            onError: (e: Exception) -> Unit
        ) {
            try {
                val response = call.invoke()
                response?.let {
                    onSuccess(it)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}