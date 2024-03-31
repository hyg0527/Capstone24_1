package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.PostListDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("post/list")
    fun getPostList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("boardName") boardName: String
    ): Call<PostListDto>
}