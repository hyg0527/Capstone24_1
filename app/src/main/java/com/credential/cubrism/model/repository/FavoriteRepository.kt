package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.FavoriteApi
import com.credential.cubrism.model.dto.FavoriteAddDto
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteRepository {
    private val favoriteApi: FavoriteApi = RetrofitClient.getRetrofitWithAuth()?.create(FavoriteApi::class.java)!!

    fun addFavorite(favoriteAddDto: FavoriteAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        favoriteApi.addFavorite(favoriteAddDto).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun deleteFavorite(code: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        favoriteApi.deleteFavorite(code).enqueue(object : Callback<MessageDto> {
            override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun getFavoriteList(callback: (ResultUtil<List<FavoriteListDto>>) -> Unit) {
        favoriteApi.getFavoriteList().enqueue(object : Callback<List<FavoriteListDto>> {
            override fun onResponse(call: Call<List<FavoriteListDto>>, response: Response<List<FavoriteListDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<List<FavoriteListDto>>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}