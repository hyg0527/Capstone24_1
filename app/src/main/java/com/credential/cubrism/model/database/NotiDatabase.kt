package com.credential.cubrism.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.credential.cubrism.model.dao.NotiDao
import com.credential.cubrism.model.entity.NotiEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(entities = [NotiEntity::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class NotiDatabase : RoomDatabase() {
    abstract fun notiDao(): NotiDao
}

class LocalDateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime): String {
        return date.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, formatter)
    }
}