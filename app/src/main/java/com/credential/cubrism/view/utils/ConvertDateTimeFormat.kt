package com.credential.cubrism.view.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ConvertDateTimeFormat {
    fun convertDateTimeFormat(date: String, originalFormat: String, targetFormat: String): String {
        val originalFormatter = DateTimeFormatter.ofPattern(originalFormat, Locale.KOREA)
        val targetFormatter = DateTimeFormatter.ofPattern(targetFormat, Locale.KOREA)
        val dateTime = LocalDateTime.parse(date, originalFormatter)
        return dateTime.format(targetFormatter)
    }

    fun convertStringToLocalDateTime(date: String, format: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(format, Locale.KOREA)
        return LocalDateTime.parse(date, formatter)
    }

    fun converLocalDateTimeToString(date: LocalDateTime, format: String): String {
        val formatter = DateTimeFormatter.ofPattern(format, Locale.KOREA)
        return date.format(formatter)
    }
}