package com.credential.cubrism.view

import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import com.credential.cubrism.R
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.CalendarAdapter
import com.credential.cubrism.view.adapter.DateSelect
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class CalendarHyg {
    private fun getInstance(): Calendar {
        return Calendar.getInstance()
    }

    fun initToday(monthTxt: TextView, currentDate: TextView, adapter: CalendarAdapter, data: ArrayList<CalMonth>? = null,
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
        val monthList = showMonthCalendar(monthPickTxt, m, w, data)
        adapter.updateCalendar(monthList)

        val selectedReturn = String.format("%02d", initYear) + " - " +
                String.format("%02d", initMonth) + " - " + String.format("%02d", initDay)
        callback(selectedReturn)
    }

    // 해당 월의 달력 출력 함수
    fun showMonthCalendar(yearMonth: String, daysInMonth: Int, dayOfWeekIndex: Int, data: ArrayList<CalMonth>? = null): ArrayList<DateSelect> {
        val (year, month) = getSelectedYearMonth(yearMonth)
        val monthData = "$year - ${String.format("%02d", month)}"

        val numDate = extractListInfo(data ?: ArrayList())
        val daysList = ArrayList<DateSelect>().apply {
            val weekOfTheDayList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            for (i in 1..7) { // 요일 출력 부분
                add(DateSelect(weekOfTheDayList[i - 1]))
            }
            for (i in 0..dayOfWeekIndex - 2) { // 1일이 나오기 전까지 공백을 메우는 부분
                add(DateSelect(" "))
            }
            for (i in 1..daysInMonth) { // 날짜 출력 부분
                add(DateSelect("$i"))
            }
        }
        // 일정 리스트와 출력하려는 월의 날짜를 모두 비교 하여 해당 날짜에 일정이 있으면 표시
        for (date in numDate) if (date.key == monthData)
            for (day in daysList)
                for (scDate in date.value)
                    if ((day.date ?: "").isDigitsOnly() && (day.date ?: "").toInt() == scDate) {
                        day.isScheduled = true
                        break
                    }

        return daysList
    }

    private fun extractListInfo(dataS: ArrayList<CalMonth>): Map<String, List<Int>> { // 월별 일정이 추가된 날을 형식에 맞도록 반환
        val dateMap: MutableMap<String, List<Int>> = mutableMapOf()

        for (data in dataS) {
            val startMonth = data.startDate!!.substring(0, 9)
            val endMonth = data.endDate!!.substring(0, 9)

            val startDate = data.startDate.substring(12, 14).toInt()
            val endDate = data.endDate.substring(12, 14).toInt()

            println(getMonthsBetween(startMonth, endMonth))
            val months = getMonthsBetween(startMonth, endMonth)
            for (month in months) {
                val dateList = dateMap[month]?.toMutableList() ?: mutableListOf()
                val lastDayOfMonth = getLastDayOfMonth(month)

                if (month == startMonth && month == endMonth) { // 시작 월과 끝 월이 동일한 경우
                    for (i in startDate..endDate)
                        dateList.add(i)
                } else if (month == startMonth) { // 시작 월 처리
                    for (i in startDate..lastDayOfMonth)
                        dateList.add(i)
                } else if (month == endMonth) { // 끝 월 처리
                    for (i in 1..endDate)
                        dateList.add(i)
                } else { // 그 외의 월 처리
                    for (i in 1..lastDayOfMonth)
                        dateList.add(i)
                }

                dateMap[month] = dateList.distinct().sorted().toMutableList()
            }
        }

        return dateMap
    }

    private fun getLastDayOfMonth(yearMonth: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy - MM")
        val yearMonthObj = YearMonth.parse(yearMonth, formatter)

        return yearMonthObj.lengthOfMonth()
    }

    private fun getMonthsBetween(start: String, end: String): List<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy - MM")
        val startDate = YearMonth.parse(start, formatter)
        val endDate = YearMonth.parse(end, formatter)

        val months = mutableListOf<String>()

        var current = startDate
        while (current.isBefore(endDate) || current == endDate) {
            months.add(current.format(formatter))
            current = current.plusMonths(1)
        }

        return months
    }

    fun getMonthInfoS(input: String): Pair<Int, Int> { // "April 2024"로 입력을 받으면, 월의 일 수, 1일의 시작 인덱스 반환
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
        val yearMonth = YearMonth.parse(input, formatter)

        // 해당 월의 첫 날과 마지막 날
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        // 첫 날의 시작 인덱스 계산
        val firstDayIndex = firstDayOfMonth.dayOfWeek.value % 7 + 1
        // 해당 월의 일 수를 계산
        val numDaysInMonth = lastDayOfMonth.dayOfMonth

        return Pair(numDaysInMonth, firstDayIndex)
    }

    fun showDatePickDialog(view: View, builder: AlertDialog.Builder, datePickTxt: TextView, txtCurrentDate: TextView,
                           adapter: CalendarAdapter, data: ArrayList<CalMonth>? = null, callback: (String) -> Unit) { // numberpicker dialog 호출
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
                val monthList = showMonthCalendar(resText, m, w, data)
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

    fun setPreNextMonthCalendar(adapter: CalendarAdapter, yearMonth: TextView, currentDate: TextView,
                                data: ArrayList<CalMonth>? = null, isPreNext: String, callback: (String) -> Unit) {
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
        val dateTxt = "${year}년 ${month}월 1일"
        currentDate.text = dateTxt
        yearMonth.text = selectedMonthToString(month) + " " + year

        val (m, w) = setDateWeek(year, month)
        val monthList = showMonthCalendar(yearMonth.text.toString(), m, w, data)
        adapter.updateCalendar(monthList)

        val selectedReturn = String.format("%02d", year) + " - " + String.format("%02d", month) + " - 01"
        callback(selectedReturn)
    }

    private fun selectedMonthToString(selectedMonth: Int): String { // 월에 해당하는 숫자를 영어로 변환
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