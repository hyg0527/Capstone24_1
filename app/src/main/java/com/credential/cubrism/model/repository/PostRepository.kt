package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.PostApi
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.CommentUpdateDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostListDto
import com.credential.cubrism.model.dto.PostMyListDto
import com.credential.cubrism.model.dto.PostUpdateDto
import com.credential.cubrism.model.dto.PostViewDto
import com.credential.cubrism.model.dto.ReplyAddDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil

class PostRepository {
    private val postApi: PostApi = RetrofitClient.getRetrofit()?.create(PostApi::class.java)!!
    private val postApiAuth: PostApi = RetrofitClient.getRetrofitWithAuth()?.create(PostApi::class.java)!!

    fun addPost(postAddDto: PostAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.addPost(postAddDto), callback)
    }

    fun deletePost(postId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.deletePost(postId), callback)
    }

    fun updatePost(postId: Int, postUpdateDto: PostUpdateDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.updatePost(postId, postUpdateDto), callback)
    }

    fun getPostView(postId: Int, callback: (ResultUtil<PostViewDto>) -> Unit) {
        handleResponse(postApi.getPostView(postId), callback)
    }

    fun getPostList(boardId: Int, page: Int, limit: Int, searchQuery: String?, callback: (ResultUtil<PostListDto>) -> Unit) {
        handleResponse(postApi.getPostList(boardId, page, limit, searchQuery), callback)
    }

    fun getMyPostList(page: Int, limit: Int, callback: (ResultUtil<PostMyListDto>) -> Unit) {
        handleResponse(postApiAuth.getMyPostList(page, limit), callback)
    }

    fun getFavoritePostList(boardId: Int, page: Int, limit: Int, callback: (ResultUtil<PostListDto>) -> Unit) {
        handleResponse(postApiAuth.getFavoritePostList(boardId, page, limit), callback)
    }

    fun addComment(commentAddDto: CommentAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.addComment(commentAddDto), callback)
    }

    fun deleteComment(commentId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.deleteComment(commentId), callback)
    }

    fun updateComment(commentId: Int, commentUpdateDto: CommentUpdateDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.updateComment(commentId, commentUpdateDto), callback)
    }

    fun addReply(replyAddDto: ReplyAddDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(postApiAuth.addReply(replyAddDto), callback)
    }
}