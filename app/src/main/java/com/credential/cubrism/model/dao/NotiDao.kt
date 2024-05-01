package com.credential.cubrism.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.credential.cubrism.model.entity.NotiEntity

@Dao
interface NotiDao {
    @Query("SELECT * FROM noti ORDER BY date DESC")
    fun getAllNoties(): LiveData<List<NotiEntity>>

    @Insert
    suspend fun insertNoti(dto: NotiEntity)

    @Query("DELETE FROM noti")
    suspend fun deleteAllNoties()
}