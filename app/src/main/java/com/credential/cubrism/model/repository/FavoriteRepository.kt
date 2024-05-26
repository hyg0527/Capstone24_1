package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.FavoriteApi
import com.credential.cubrism.model.dto.FavoriteAddDto
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil

class FavoriteRepository {
    private val favoriteApi: FavoriteApi = RetrofitClient.getRetrofitWithAuth()?.create(FavoriteApi::class.java)!!

    fun addFavorite(favoriteAddDto: FavoriteAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(favoriteApi.addFavorite(favoriteAddDto), callback)
    }

    fun deleteFavorite(code: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(favoriteApi.deleteFavorite(code), callback)
    }

    fun getFavoriteList(callback: (ResultUtil<List<FavoriteListDto>>) -> Unit) {
        handleResponse(favoriteApi.getFavoriteList(), callback)
    }
}