package com.credential.cubrism.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.credential.cubrism.model.entity.QualEntity

@Dao
interface QualDao {
    @Query("SELECT * FROM qual ORDER BY name ASC")
    fun getAllQuals(): List<QualEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQual(dto: QualEntity)

    @Delete
    suspend fun deleteQual(dto: QualEntity)
}