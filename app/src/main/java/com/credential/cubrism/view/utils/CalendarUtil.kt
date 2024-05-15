package com.credential.cubrism.view.utils

import com.credential.cubrism.view.adapter.DateSelect
import java.time.LocalDate
import java.util.Calendar

class CalendarUtil {
    private fun getInstance(): Calendar = Calendar.getInstance()

    private val weekOfTheDayList = listOf("일", "월", "화", "수", "목", "금", "토")

    fun initToday(): Triple<Int, Int, Int> { // 오늘 날짜로 돌아오는 함수
        val calInstance = getInstance()
        val initYear = calInstance.get(Calendar.YEAR) // 처음 뷰 생성시 오늘날짜로 초기화
        val initMonth = calInstance.get(Calendar.MONTH) + 1  // 월은 0부터 시작하므로 1+.
        val initDay = calInstance.get(Calendar.DAY_OF_MONTH)

        return Triple(initYear, initMonth, initDay)
    }

    // 해당 달의 달력 출력 함수
    fun showCalendar(year: Int, month: Int, day: Int? = null) : MutableList<DateSelect> {
        val (daysInMonth, dayOfWeekIndex) = setDateWeek(year, month)
        return mutableListOf<DateSelect>().apply {
            for (i in 1..7) { // 요일 출력 부분
                add(DateSelect(date = weekOfTheDayList[i - 1]))
            }
            for (i in 0..dayOfWeekIndex - 2) { // 1일이 나오기 전까지 공백을 메우는 부분
                add(DateSelect(null))
            }
            for (i in 1..daysInMonth) { // 날짜 출력 부분
                val calInstance = getInstance()
                calInstance.set(year, month - 1, i)
                val dayOfWeek = calInstance.get(Calendar.DAY_OF_WEEK)

                if (day != null && i == day) {
                    add(DateSelect(date = "$i", year = year, month = month, isHighlighted = true, dayOfWeek = dayOfWeek))
                } else if (day == null && i == 1) {
                    add(DateSelect(date = "$i", year = year, month = month,  isHighlighted = true, dayOfWeek = dayOfWeek))
                } else {
                    add(DateSelect(date = "$i", year = year, month = month,  dayOfWeek = dayOfWeek))
                }
            }
        }
    }

    // 선택된 날짜의 일수와 요일 반환
    private fun setDateWeek(year: Int, month: Int): Pair<Int, Int> {
        val calInstance = getInstance()
        calInstance.set(year, month - 1, 1)

        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월의 마지막 날짜
        val dayOfWeek = calInstance.get(Calendar.DAY_OF_WEEK) // 1일의 요일 저장

        return Pair(daysInMonth, dayOfWeek)
    }

    fun getPrevNextMonth(yearMonth: String, isPreNext: String): Pair<Int, Int> {
        var (year, month) = getSelectedYearMonth(yearMonth)

        when (isPreNext) {
            "prev" -> {
                if (month == 1) { year--; month = 12 }
                else month--
            }
            "next" -> {
                if (month == 12) { year++; month = 1 }
                else month++
            }
        }

        return Pair(year, month)
    }

    fun getSelectedYearMonth(yearMonth: String): Pair<Int, Int> {
        val (year, month) = yearMonth.replace("년", "").replace("월", "").split(" ")
        return Pair(year.toInt(), month.toInt())
    }

    fun getSelectedYearMonthDay(yearMonthDay: String): LocalDate {
        val (year, month, day) = yearMonthDay.replace("년", "").replace("월", "").replace("일", "").split(" ")
        return LocalDate.of(year.toInt(), month.toInt(), day.toInt())
    }
}