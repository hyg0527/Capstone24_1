package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.PostApi
import com.credential.cubrism.model.dto.PostListDto
import com.credential.cubrism.model.dto.PostViewDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepository {
    private val postApi: PostApi = RetrofitClient.getRetrofit()?.create(PostApi::class.java)!!

    fun getPostList(page: Int, limit: Int, boardName: String, searchQuery: String?, callback: (ResultUtil<PostListDto>) -> Unit) {
        postApi.getPostList(page, limit, boardName, searchQuery).enqueue(object : Callback<PostListDto> {
            override fun onResponse(call: Call<PostListDto>, response: Response<PostListDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<PostListDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun getPostView(boardName: String, postId: Int, callback: (ResultUtil<PostViewDto>) -> Unit) {
        postApi.getPostView(boardName, postId).enqueue(object : Callback<PostViewDto> {
            override fun onResponse(call: Call<PostViewDto>, response: Response<PostViewDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<PostViewDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}