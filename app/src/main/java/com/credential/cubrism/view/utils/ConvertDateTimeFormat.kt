package com.credential.cubrism.view.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ConvertDateTimeFormat {
    fun convertDateTimeFormat(date: String, originalFormat: String, targetFormat: String, locale: Locale): String {
        val originalFormatter = DateTimeFormatter.ofPattern(originalFormat, locale)
        val targetFormatter = DateTimeFormatter.ofPattern(targetFormat, locale)
        val dateTime = LocalDateTime.parse(date, originalFormatter)
        return dateTime.format(targetFormatter)
    }

    fun convertStringToLocalDateTime(date: String, format: String, locale: Locale): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(format, locale)
        return LocalDateTime.parse(date, formatter)
    }

    fun converLocalDateTimeToString(date: LocalDateTime, format: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(format, locale)
        return date.format(formatter)
    }
}