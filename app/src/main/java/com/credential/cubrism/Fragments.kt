package com.credential.cubrism

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CalendarView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudyFragment : Fragment(R.layout.fragment_study) {

}

class CalFragment : Fragment(R.layout.fragment_cal) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_cal_month 으로 설정
        if (savedInstanceState == null) {
            replaceFragment(CalMonthFragment())
        }

        val toggleMonth = view.findViewById<ToggleButton>(R.id.toggleMonth)
        val toggleWeek = view.findViewById<ToggleButton>(R.id.toggleWeek)
        toggleMonth.isChecked = true

        toggleMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toggleWeek.isChecked = false
                replaceFragment(CalMonthFragment())
            }
        }
        toggleWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toggleMonth.isChecked = false
                replaceFragment(CalWeekFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.calFragmentContainerView, fragment)
            .setReorderingAllowed(true)
            .commit()
    }
}

class CalMonthFragment : Fragment(R.layout.fragment_cal_month) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val currentDate = view.findViewById<TextView>(R.id.txtCurrentDate)

        // 날짜 누르면 날짜를 textview에 출력
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            currentDate.text = selectedDate
        }

        // 처음 생성시 오늘 날짜 기본 출력
        val today = Calendar.getInstance().time
        val todayDateFormat = SimpleDateFormat("yyyy년 M월 dd일", Locale.getDefault())
        val todayString = todayDateFormat.format(today)

        currentDate.text = todayString
    }
}

class CalWeekFragment : Fragment(R.layout.fragment_cal_week) {
    private lateinit var datePickTxt: TextView
    private lateinit var txtCurrentDateWeek: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datePickTxt = view.findViewById(R.id.txtYearMonth)
        txtCurrentDateWeek = view.findViewById(R.id.txtCurrentDateWeek)

        val (year, month) = getYearMonth()
        val monthString = selectedMonthToString(month)
        datePickTxt.text = "$monthString $year"

        val calInstance = Calendar.getInstance() // 현재 날짜에 대한 일수, 요일 정보 추출
        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
        calInstance.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeekIndex = calInstance.get(Calendar.DAY_OF_WEEK)
        val weekList = showWeekCalendar(daysInMonth, dayOfWeekIndex)

        initToday()

        val calWeekView = view.findViewById<RecyclerView>(R.id.calWeekView)
        val calWeekAdapter = CalWeekAdapter(weekList)
        calWeekView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        calWeekView.adapter = calWeekAdapter

        calWeekAdapter.setItemClickListener(object: DateWeekClickListener {
            override fun onItemClick(item: DateWeek) {
                val day = item.date.toString()
                val regex = Regex("\\d+일")
                val text = txtCurrentDateWeek.text.toString().replace(regex, "${day}일")
                // 달력의 날짜 누르면 textview 날짜 갱신
                txtCurrentDateWeek.text = text
            }
        })

        datePickTxt.setOnClickListener { // 연월 변경 다이얼로그 호출
            showDatePickDialog(calWeekAdapter)
        }

        val returnToday = view.findViewById<TextView>(R.id.txtTodayBtn)
        returnToday.setOnClickListener {  // today버튼 누르면 오늘로 돌아옴.
            initToday()
            // 연월과 달력도 같이 돌아오는 부분 추가
            datePickTxt.text = "$monthString $year"
            val changedCalendar = showWeekCalendar(daysInMonth, dayOfWeekIndex)
            calWeekAdapter.updateCalendar(changedCalendar)
        }
    }

    private fun initToday() { // 오늘 날짜로 돌아오는 함수 (textview만)
        val calInstance = Calendar.getInstance()
        val initYear = calInstance.get(Calendar.YEAR) // 처음 뷰 생성시 오늘날짜로 초기화
        val initMonth = calInstance.get(Calendar.MONTH) + 1  // 월은 0부터 시작하므로 1+.
        val initDay = calInstance.get(Calendar.DAY_OF_MONTH)

        txtCurrentDateWeek.text = "${initYear}년 ${initMonth}월 ${initDay}일"
    }

    private fun showWeekCalendar(daysInMonth: Int, dayOfWeek: Int): ArrayList<DateWeek> { // 해당 월의 달력 출력 함수
        val dayOfTheWeekList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val weekList = ArrayList<DateWeek>().apply {
            for (i in 1..daysInMonth) {
                val dayWeek = dayOfTheWeekList[(dayOfWeek + i - 2) % dayOfTheWeekList.size]
                add(DateWeek(i, dayWeek))
            }
        }
        return weekList
    }

    private fun getYearMonth(): Pair<Int, Int> { // 현재 날짜의 연, 월을 반환
        val current = Calendar.getInstance()
        val getYear = current.get(Calendar.YEAR)
        val getMonth = current.get(Calendar.MONTH) + 1

        return Pair(getYear, getMonth)
    }

    private fun showDatePickDialog(calWeekAdapter: CalWeekAdapter) { // numberpicker dialog 호출
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_date_pick, null)

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
                datePickTxt.text = "$month $selectedYear"
                // 선택후 달력이 바뀌는 부분 여기에 추가
                val (m, w) = setDateWeek(selectedYear, selectedMonth)
                val weekList = showWeekCalendar(m, w)
                txtCurrentDateWeek.text = "${selectedYear}년 ${selectedMonth}월 1일" // 1일로 초기화(하드코딩..)

                // RecyclerView에 데이터 갱신
                calWeekAdapter.updateCalendar(weekList)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // 취소 버튼을 클릭했을 때의 동작
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun setDateWeek(year: Int, month: Int): Pair<Int, Int> { // 선택된 날짜의 일수와 요일 반환
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.YEAR, year)
        calInstance.set(Calendar.MONTH, month - 1)

        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월의 마지막 날짜
        calInstance.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeek = calInstance.get(Calendar.DAY_OF_WEEK) // 1일의 요일 저장
        Toast.makeText(requireContext(), "$daysInMonth $dayOfWeek", Toast.LENGTH_SHORT).show()

        return Pair(daysInMonth, dayOfWeek)
    }

    private fun getSelectedYearMonth(yearMonth: String): Pair<Int, Int> { // 다이얼로그를 다시 열었을때 현재 선택된 연월이 다시 표시되는 함수
        val (month, year) = yearMonth.split(" ")
        val returnYear = year.toInt()
        val returnMonth = selectedMonthToInt(month)

        return Pair(returnYear, returnMonth)
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
    // else에 대한 예외처리를 해야하는데 일단 패스
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

class MyPageFragment : Fragment(R.layout.fragment_mypage) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileFix = view.findViewById<CircleImageView>(R.id.circle1)

        profileFix.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileFixActivity::class.java)
            startActivity(intent)
        }
    }
}