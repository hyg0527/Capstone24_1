package com.credential.cubrism.model.utils

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NetworkUtil {
    fun <T> handleResponse(call: Call<T>, callback: (ResultUtil<T>) -> Unit) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        callback(ResultUtil.Error(JSONObject(it).optString("message")))
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}