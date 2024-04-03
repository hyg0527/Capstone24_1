package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.MajorFieldDto
import com.credential.cubrism.model.dto.MiddleFieldDto
import com.credential.cubrism.model.dto.QualificationDetailsDto
import com.credential.cubrism.model.dto.QualificationListDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QualificationApi {
    @GET("/qualification/list/all")
    fun getQualificationList(): Call<List<QualificationListDto>>

    @GET("/qualification/list/majorfield")
    fun getMajorFieldList(): Call<List<MajorFieldDto>>

    @GET("/qualification/list/middlefield")
    fun getMiddleFieldList(@Query("field") type: String): Call<List<MiddleFieldDto>>

    @GET("/qualification/details")
    fun getQualificationDetails(@Query("code") code: String): Call<QualificationDetailsDto>
}