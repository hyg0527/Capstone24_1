package com.credential.cubrism.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.credential.cubrism.model.dao.QualDao
import com.credential.cubrism.model.entity.QualEntity

@Database(entities = [QualEntity::class], version = 1)
abstract class QualDatabase : RoomDatabase() {
    abstract fun qualDao(): QualDao
}