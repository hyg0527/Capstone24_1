package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.CommentUpdateDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostListDto
import com.credential.cubrism.model.dto.PostViewDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    // 게시글 작성
    @POST("/post")
    fun addPost(@Body postAddDto: PostAddDto): Call<MessageDto>

    // 게시글 삭제
//    @DELETE("/post/{postId}")
//    fun deletePost(@Path("postId") postId: Int): Call<MessageDto>

    // 게시글 수정
//    @PUT("/post/{postId}")
//    fun updatePost(@Path("postId") postId: Int, @Body postUpdateDto: PostUpdateDto): Call<MessageDto>

    // 게시글 보기
    @GET("/post/{postId}")
    fun getPostView(@Path("postId") postId: Int): Call<PostViewDto>

    // 게시글 목록
    @GET("/posts")
    fun getPostList(
        @Query("board-id") boardId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search-query") searchQuery: String?
    ): Call<PostListDto>

    // 내 게시글 목록
//    @GET("/posts/my")
//    fun getMyPostList(
//        @Query("page") page: Int,
//        @Query("limit") limit: Int
//    ): Call<PostListDto>

    // 관심 자격증 게시글 목록
    @GET("/posts/favorites")
    fun getFavoritePostList(
        @Query("board-id") boardId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<PostListDto>

    // 댓글 추가
    @POST("/comment")
    fun addComment(@Body commentAddDto: CommentAddDto): Call<MessageDto>

    // 댓글 삭제
    @DELETE("/comment/{commentId}")
    fun deleteComment(@Path("commentId") commentId: Int): Call<MessageDto>

    // 댓글 수정
    @PUT("/comment/{commentId}")
    fun updateComment(@Path("commentId") commentId: Int, @Body commentUpdateDto: CommentUpdateDto): Call<MessageDto>

    // 대댓글 추가
//    @POST("/reply")
//    fun addReply(@Body replyAddDto: ReplyAddDto): Call<MessageDto>

    // 대댓글 삭제
//    @DELETE("/reply/{replyId}")
//    fun deleteReply(@Path("replyId") replyId: Int): Call<MessageDto>

    // 대댓글 수정
//    @PUT("/reply/{replyId}")
//    fun updateReply(@Path("replyId") replyId: Int, @Body replyUpdateDto: ReplyUpdateDto): Call<MessageDto>
}