package com.credential.cubrism.model.dto

data class QualificationDetailsDto(
    val code: String,
    val name: String,
    val schedule: List<Schedule>,
    val fee: Fee,
    val tendency: String?,
    val standard: List<File>,
    val question: List<File>,
    val acquisition: String?,
    val books: List<Book>
)

data class Schedule(
    val category: String,
    val writtenApp: String?,
    val writtenExam: String?,
    val writtenExamResult: String?,
    val practicalApp: String,
    val practicalExam: String,
    val practicalExamResult: String
)

data class Fee(
    val writtenFee: Int?,
    val practicalFee: Int?
)

data class File(
    val filePath: String,
    val fileName: String
)

data class Book(
    val title: String,
    val authors: String?,
    val publisher: String,
    val date: String,
    val price: Int,
    val salePrice: Int?,
    val thumbnail: String?,
    val url: String
)