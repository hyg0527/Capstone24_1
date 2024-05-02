package com.credential.cubrism.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qual")
data class QualEntity(
    @PrimaryKey val code: String,
    val name: String
)