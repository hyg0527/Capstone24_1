package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.PostApi
import com.credential.cubrism.model.dto.PostListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepository {
    private val postApi: PostApi = RetrofitClient.getRetrofit()?.create(PostApi::class.java)!!

    fun getPostList(page: Int, limit: Int, boardName: String, callback: (ResultUtil<PostListDto>) -> Unit) {
        postApi.getPostList(page, limit, boardName).enqueue(object : Callback<PostListDto> {
            override fun onResponse(call: Call<PostListDto>, response: Response<PostListDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(ResultUtil.Success(it))
                    }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).getString("message"))) }
                }
            }

            override fun onFailure(call: Call<PostListDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }
}