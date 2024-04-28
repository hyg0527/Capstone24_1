package com.credential.cubrism.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "noti")
data class NotiEntity(
    @PrimaryKey(autoGenerate = true) val notiId: Long = 0,
    val title: String,
    val body: String,
    val type: String,
    val id: String,
    val date: LocalDateTime = LocalDateTime.now()
)