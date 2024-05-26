package com.credential.cubrism.model.repository

import com.credential.cubrism.model.dao.NotiDao
import com.credential.cubrism.model.entity.NotiEntity

class NotiRepository(private val notiDao: NotiDao) {
    fun getAllNoties() = notiDao.getAllNoties()

    suspend fun insertNoti(dto: NotiEntity) = notiDao.insertNoti(dto)

    suspend fun deleteAllNoties() = notiDao.deleteAllNoties()
}