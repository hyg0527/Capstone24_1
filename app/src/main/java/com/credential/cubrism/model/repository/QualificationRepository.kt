package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.QualificationApi
import com.credential.cubrism.model.dto.MajorFieldDto
import com.credential.cubrism.model.dto.MiddleFieldDto
import com.credential.cubrism.model.dto.QualificationDetailsDto
import com.credential.cubrism.model.dto.QualificationListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil

class QualificationRepository {
    private val qualificationApi: QualificationApi = RetrofitClient.getRetrofit()?.create(QualificationApi::class.java)!!

    fun qualificationList(callback: (ResultUtil<List<QualificationListDto>>) -> Unit) {
        handleResponse(qualificationApi.getQualificationList(), callback)
    }

    fun majorFieldList(callback: (ResultUtil<List<MajorFieldDto>>) -> Unit) {
        handleResponse(qualificationApi.getMajorFieldList(), callback)
    }

    fun middleFieldList(field: String, callback: (ResultUtil<List<MiddleFieldDto>>) -> Unit) {
        handleResponse(qualificationApi.getMiddleFieldList(field), callback)
    }

    fun qualificationDetails(code: String, callback: (ResultUtil<QualificationDetailsDto>) -> Unit) {
        handleResponse(qualificationApi.getQualificationDetails(code), callback)
    }
}