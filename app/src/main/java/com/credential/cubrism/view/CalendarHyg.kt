package com.credential.cubrism.view

import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.view.adapter.CalendarAdapter
import com.credential.cubrism.view.adapter.DateSelect
import java.util.Calendar

class CalendarHyg {
    private var isActivated = false
    fun getInstance(): Calendar {
        return Calendar.getInstance()
    }

    fun initToday(monthTxt: TextView, currentDate: TextView, adapter: CalendarAdapter,
                  callback: (String) -> Unit) { // 오늘 날짜로 돌아오는 함수
        val calInstance = getInstance()
        val initYear = calInstance.get(Calendar.YEAR) // 처음 뷰 생성시 오늘날짜로 초기화
        val initMonth = calInstance.get(Calendar.MONTH) + 1  // 월은 0부터 시작하므로 1+.
        val initDay = calInstance.get(Calendar.DAY_OF_MONTH)

        val todayTxt = "${initYear}년 ${initMonth}월 ${initDay}일"
        val monthPickTxt = "${selectedMonthToString(initMonth)} ${initYear}"
        currentDate.text = todayTxt
        monthTxt.text = monthPickTxt

        val (m, w) = setDateWeek(initYear, initMonth)
        val monthList = showMonthCalendar(m, w)
        adapter.updateCalendar(monthList)

        val selectedReturn = String.format("%02d", initYear) + " - " +
                String.format("%02d", initMonth) + " - " + String.format("%02d", initDay)
        callback(selectedReturn)
    }

    fun showMonthCalendar(daysInMonth: Int, dayOfWeekIndex: Int): ArrayList<DateSelect> { // 해당 월의 달력 출력 함수(월간)
        val daysList = ArrayList<DateSelect>().apply {
            val weekOfTheDayList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            for (i in 1..7) { // 요일 출력 부분
                add(DateSelect(weekOfTheDayList[i - 1]))
            }
            for (i in 0..dayOfWeekIndex - 2) { // 1일이 나오기 전까지 공백을 메우는 부분
                add(DateSelect(" "))
            }
            if (!isActivated) { // 날짜 추가 부분(1일부터)
                isActivated = true
                for (i in 1..daysInMonth) {
                    add(DateSelect("$i"))
                }
            } else {
                add(DateSelect("1", isHighlighted = true)) // 첫 달의 1일이 디폴트 값으로 선택 되도록
                for (i in 2..daysInMonth) {
                    add(DateSelect("$i"))
                }
            }
        }

        return daysList
    }

    fun getYearMonth(): Pair<Int, Int> { // 현재 날짜의 연, 월을 반환
        val current = Calendar.getInstance()
        val getYear = current.get(Calendar.YEAR)
        val getMonth = current.get(Calendar.MONTH) + 1

        return Pair(getYear, getMonth)
    }

    fun showDatePickDialog(view: View, builder: AlertDialog.Builder, datePickTxt: TextView,
                           txtCurrentDate: TextView, adapter: CalendarAdapter, callback: (String) -> Unit) { // numberpicker dialog 호출
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
                val monthList = showMonthCalendar(m, w)
                adapter.updateCalendar(monthList)

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

    private fun setDateWeek(year: Int, month: Int): Pair<Int, Int> { // 선택된 날짜의 일수와 요일 반환
        val calInstance = getInstance()
        calInstance.set(year, month - 1, 1)

        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월의 마지막 날짜
        val dayOfWeek = calInstance.get(Calendar.DAY_OF_WEEK) // 1일의 요일 저장

        return Pair(daysInMonth, dayOfWeek)
    }

    fun setPreNextMonthCalendar(adapter: CalendarAdapter, yearMonth: TextView,
                                currentDate: TextView, isPreNext: String, callback: (String) -> Unit) {
        var (year, month) = getSelectedYearMonth(yearMonth.text.toString())

        when (isPreNext) {
            "pre" -> {
                if (month == 1) { year--; month = 12 }
                else month--
            }
            "next" -> {
                if (month == 12) { year++; month = 1 }
                else month++
            }
            else -> { Log.d("invalid type", "이전 이후 선택 부분 타입 오류") }
        }

        val (m, w) = setDateWeek(year, month)
        val monthList = showMonthCalendar(m, w)
        adapter.updateCalendar(monthList)

        val dateTxt = "${year}년 ${month}월 1일"
        currentDate.text = dateTxt
        yearMonth.text = selectedMonthToString(month) + " " + year

        val selectedReturn = String.format("%02d", year) + " - " + String.format("%02d", month) + " - 01"
        callback(selectedReturn)
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

    private fun selectedMonthToInt(selectedMonth: String): Int { // 월애 해당하는 문자열을 숫자로 반환
        return when (selectedMonth) {
            "January" -> 1; "February" -> 2; "March" -> 3
            "April" -> 4;   "May" -> 5;     "June" -> 6
            "July" -> 7;    "August" -> 8;  "September" -> 9
            "October" -> 10; "November" -> 11; "December" -> 12
            else -> 0
        }
    }
}