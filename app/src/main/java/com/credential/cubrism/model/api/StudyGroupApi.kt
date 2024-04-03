package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.StudyGroupListDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StudyGroupApi {
    @GET("/studygroup/list")
    fun getStudyGroupList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("recruiting") recruiting: Boolean
    ): Call<StudyGroupListDto>
}