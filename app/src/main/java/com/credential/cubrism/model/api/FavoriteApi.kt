package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.FavoriteAddDto
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.model.dto.MessageDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {
    // 관심 자격증 추가
    @POST("/favorite")
    fun addFavorite(@Body favoriteAddDto: FavoriteAddDto): Call<MessageDto>

    // 관심 자격증 삭제
    @DELETE("/favorite/{code}")
    fun deleteFavorite(@Path("code") code: Int): Call<MessageDto>

    // 관심 자격증 목록
    @GET("/favorites")
    fun getFavoriteList(): Call<List<FavoriteListDto>>
}