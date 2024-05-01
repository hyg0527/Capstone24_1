package com.credential.cubrism.view.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConvertDateTimeFormat {
    fun convertDateTimeFormat(dateStr: String, originalFormat: String, targetFormat: String): String {
        val originalFormatter = DateTimeFormatter.ofPattern(originalFormat)
        val targetFormatter = DateTimeFormatter.ofPattern(targetFormat)
        val dateTime = LocalDateTime.parse(dateStr, originalFormatter)
        return dateTime.format(targetFormatter)
    }
}