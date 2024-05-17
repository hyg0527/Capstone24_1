package com.credential.cubrism.view.utils

import com.credential.cubrism.view.adapter.DateSelect
import java.time.LocalDate
import java.util.Calendar

class CalendarUtil {
    private fun getInstance(): Calendar = Calendar.getInstance()

    private val weekOfTheDayList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    fun initToday(): Triple<Int, String, Int> { // 오늘 날짜로 돌아오는 함수
        val calInstance = getInstance()
        val initYear = calInstance.get(Calendar.YEAR) // 처음 뷰 생성시 오늘날짜로 초기화
        val initMonth = calInstance.get(Calendar.MONTH) + 1  // 월은 0부터 시작하므로 1+.
        val initDay = calInstance.get(Calendar.DAY_OF_MONTH)

        return Triple(initYear, selectedMonthToString(initMonth), initDay)
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
        val (month, year) = yearMonth.split(" ")
        val returnYear = year.toInt()
        val returnMonth = selectedMonthToInt(month)

        return Pair(returnYear, returnMonth)
    }

    fun getSelectedYearMonthDay(yearMonthDay: String): LocalDate {
        val (year, month, day) = yearMonthDay.replace("년", "").replace("월", "").replace("일", "").split(" ")
        return LocalDate.of(year.toInt(), month.toInt(), day.toInt())
    }

    fun selectedMonthToString(selectedMonth: Int): String { // 월에 해당하는 숫자를 영어로 변환
        return when (selectedMonth) {
            1 -> "January";     2 -> "February";    3 -> "March"
            4 -> "April";       5 -> "May";         6 -> "June"
            7 -> "July";        8 -> "August";      9 -> "September"
            10 -> "October";    11 -> "November";   12 -> "December"
            else -> "Invalid Month"
        }
    }

    fun selectedMonthToInt(selectedMonth: String): Int { // 월애 해당하는 문자열을 숫자로 반환
        return when (selectedMonth) {
            "January" -> 1;     "February" -> 2;    "March" -> 3
            "April" -> 4;       "May" -> 5;         "June" -> 6
            "July" -> 7;        "August" -> 8;      "September" -> 9
            "October" -> 10;    "November" -> 11;   "December" -> 12
            else -> 0
        }
    }
}