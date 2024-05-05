package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.ChatResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {
    @GET("/studygroup/{studygroupId}/chats")
    fun getChattingList(@Path("studygroupId") studygroupId: Long): Call<List<ChatResponse>>
}