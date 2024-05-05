package com.credential.cubrism.model.repository

import com.credential.cubrism.model.dao.QualDao
import com.credential.cubrism.model.entity.QualEntity

class QualRepository(private val qualDao: QualDao) {
    fun getAllQuals() = qualDao.getAllQuals()

    suspend fun insertQual(dto: QualEntity) = qualDao.insertQual(dto)

    suspend fun deleteQual(dto: QualEntity) = qualDao.deleteQual(dto)
}