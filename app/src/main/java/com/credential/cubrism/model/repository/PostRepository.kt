package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.PostApi
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.CommentUpdateDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostListDto
import com.credential.cubrism.model.dto.PostUpdateDto
import com.credential.cubrism.model.dto.PostViewDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.ResultUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepository {
    private val postApi: PostApi = RetrofitClient.getRetrofit()?.create(PostApi::class.java)!!
    private val postApiAuth: PostApi = RetrofitClient.getRetrofitWithAuth()?.create(PostApi::class.java)!!

    fun addPost(postAddDto: PostAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        postApiAuth.addPost(postAddDto).enqueue(object : Callback<MessageDto> {
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

    fun deletePost(postId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        postApiAuth.deletePost(postId).enqueue(object : Callback<MessageDto> {
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

    fun updatePost(postId: Int, postUpdateDto: PostUpdateDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        postApiAuth.updatePost(postId, postUpdateDto).enqueue(object : Callback<MessageDto> {
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

    fun getPostView(postId: Int, callback: (ResultUtil<PostViewDto>) -> Unit) {
        postApi.getPostView(postId).enqueue(object : Callback<PostViewDto> {
            override fun onResponse(call: Call<PostViewDto>, response: Response<PostViewDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<PostViewDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun getPostList(boardId: Int, page: Int, limit: Int, searchQuery: String?, callback: (ResultUtil<PostListDto>) -> Unit) {
        postApi.getPostList(boardId, page, limit, searchQuery).enqueue(object : Callback<PostListDto> {
            override fun onResponse(call: Call<PostListDto>, response: Response<PostListDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<PostListDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun getFavoritePostList(boardId: Int, page: Int, limit: Int, callback: (ResultUtil<PostListDto>) -> Unit) {
        postApiAuth.getFavoritePostList(boardId, page, limit).enqueue(object : Callback<PostListDto> {
            override fun onResponse(call: Call<PostListDto>, response: Response<PostListDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(ResultUtil.Success(it)) }
                } else {
                    response.errorBody()?.string()?.let { callback(ResultUtil.Error(JSONObject(it).optString("message"))) }
                }
            }

            override fun onFailure(call: Call<PostListDto>, t: Throwable) {
                callback(ResultUtil.NetworkError())
            }
        })
    }

    fun addComment(commentAddDto: CommentAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        postApiAuth.addComment(commentAddDto).enqueue(object : Callback<MessageDto> {
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

    fun deleteComment(commentId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        postApiAuth.deleteComment(commentId).enqueue(object : Callback<MessageDto> {
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

    fun updateComment(commentId: Int, commentUpdateDto: CommentUpdateDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        postApiAuth.updateComment(commentId, commentUpdateDto).enqueue(object : Callback<MessageDto> {
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
}