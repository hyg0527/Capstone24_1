package com.credential.cubrism

import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class CalendarHyg {
    fun getInstance(): Calendar {
        return Calendar.getInstance()
    }

    fun initToday(): String { // 오늘 날짜로 돌아오는 함수
        val calInstance = Calendar.getInstance()
        val initYear = calInstance.get(Calendar.YEAR) // 처음 뷰 생성시 오늘날짜로 초기화
        val initMonth = calInstance.get(Calendar.MONTH) + 1  // 월은 0부터 시작하므로 1+.
        val initDay = calInstance.get(Calendar.DAY_OF_MONTH)

        return "${initYear}년 ${initMonth}월 ${initDay}일"
    }

    fun showMonthCalendar(daysInMonth: Int, dayOfWeekIndex: Int): ArrayList<DateSelect> { // 해당 월의 달력 출력 함수(월간)
        val daysList = ArrayList<DateSelect>().apply {
            val weekOfTheDayList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            for (i in 1..7) {
                add(DateSelect(weekOfTheDayList[i - 1], false))
            }
            for (i in 0..dayOfWeekIndex - 2) {
                add(DateSelect(" ", false))
            }
            for (i in 1..daysInMonth) {
                add(DateSelect("$i", true))
            }
        }

        return daysList
    }

    fun showWeekCalendar(daysInMonth: Int, dayOfWeek: Int): ArrayList<DateWeek> { // 해당 월의 달력 출력 함수(주간)
        val dayOfTheWeekList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val weekList = ArrayList<DateWeek>().apply {
            for (i in 1..daysInMonth) {
                val dayWeek = dayOfTheWeekList[(dayOfWeek + i - 2) % dayOfTheWeekList.size]
                add(DateWeek(i, dayWeek))
            }
        }
        return weekList
    }

    fun getYearMonth(): Pair<Int, Int> { // 현재 날짜의 연, 월을 반환
        val current = Calendar.getInstance()
        val getYear = current.get(Calendar.YEAR)
        val getMonth = current.get(Calendar.MONTH) + 1

        return Pair(getYear, getMonth)
    }

    fun showDatePickDialog(view: View, builder: AlertDialog.Builder, datePickTxt: TextView,
                           txtCurrentDate: TextView, adapter: RecyclerView.Adapter<*>, callback: (String) -> Unit) { // numberpicker dialog 호출
        val yearPick = view.findViewById<NumberPicker>(R.id.yearPick)
        val monthPick = view.findViewById<NumberPicker>(R.id.monthPick)
        val (getYear, getMonth) = getSelectedYearMonth(datePickTxt.text.toString())

        yearPick.minValue = 1950; yearPick.maxValue = 2050; yearPick.value = getYear// 범위, 기본값 설정
        monthPick.minValue = 1; monthPick.maxValue = 12; monthPick.value = getMonth // 월은 현재 달 표시
        monthPick.displayedValues = arrayOf("January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December")

        yearPick.wrapSelectorWheel = false // 년은 순환 못하게, 월은 순환 가능하게 설정
        monthPick.wrapSelectorWheel = true

        builder.setView(view).setTitle("연월 선택")
            .setPositiveButton("OK") { dialog, _ ->
                val selectedYear = yearPick.value
                val selectedMonth = monthPick.value
                val month = selectedMonthToString(selectedMonth)

                // 선택한 연, 월을 textview에 출력
                val resText = "$month $selectedYear"
                datePickTxt.text = resText

                // 선택후 달력이 바뀌는 부분 여기에 추가
                val (m, w) = setDateWeek(selectedYear, selectedMonth)
                val resTextDate = "${selectedYear}년 ${selectedMonth}월 1일" // 1일로 초기화
                txtCurrentDate.text = resTextDate

                // RecyclerView에 데이터 갱신
                if (adapter is CalWeekAdapter) {
                    val weekList = showWeekCalendar(m, w)
                    adapter.updateCalendar(weekList)
                }
                if (adapter is CalendarAdapter) {
                    val monthList = showMonthCalendar(m, w)
                    adapter.updateCalendar(monthList)
                }

                val selectedReturn = String.format("%02d", selectedYear) + " - " + String.format("%02d", selectedMonth) + " - 01"
                callback(selectedReturn)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // 취소 버튼을 클릭했을 때의 동작
                dialog.dismiss()
            }
    }

    private fun getSelectedYearMonth(yearMonth: String): Pair<Int, Int> { // 다이얼로그를 다시 열었을때 현재 선택된 연월이 다시 표시되는 함수
        val (month, year) = yearMonth.split(" ")
        val returnYear = year.toInt()
        val returnMonth = selectedMonthToInt(month)

        return Pair(returnYear, returnMonth)
    }

    fun setDateWeek(year: Int, month: Int): Pair<Int, Int> { // 선택된 날짜의 일수와 요일 반환
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.YEAR, year)
        calInstance.set(Calendar.MONTH, month - 1)

        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월의 마지막 날짜
        calInstance.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeek = calInstance.get(Calendar.DAY_OF_WEEK) // 1일의 요일 저장

        return Pair(daysInMonth, dayOfWeek)
    }

    fun selectedMonthToString(selectedMonth: Int): String { // 월에 해당하는 숫자를 영어로 변환
        return when (selectedMonth) {
            1 -> "January";  2 -> "February"; 3 -> "March"
            4 -> "April";    5 -> "May";     6 -> "June"
            7 -> "July";     8 -> "August";  9 -> "September"
            10 -> "October"; 11 -> "November"; 12 -> "December"
            else -> "Invalid Month"
        }
    }

    fun selectedMonthToInt(selectedMonth: String): Int { // 월애 해당하는 문자열을 숫자로 반환
        return when (selectedMonth) {
            "January" -> 1; "February" -> 2; "March" -> 3
            "April" -> 4;   "May" -> 5;     "June" -> 6
            "July" -> 7;    "August" -> 8;  "September" -> 9
            "October" -> 10; "November" -> 11; "December" -> 12
            else -> 0
        }
    }
}