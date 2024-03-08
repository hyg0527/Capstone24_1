package com.credential.cubrism

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalFragment : Fragment(R.layout.fragment_cal) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_cal_month 으로 설정
        if (savedInstanceState == null) {
            replaceFragment(CalMonthFragment(), "CalMonth")
        }

        val toggleMonth = view.findViewById<ToggleButton>(R.id.toggleMonth) // 주간, 월간보기 선택버튼
        val toggleWeek = view.findViewById<ToggleButton>(R.id.toggleWeek)
        val addSchedule = view.findViewById<ImageView>(R.id.btnAddSchedule) // 일정 추가 버튼
        toggleMonth.isChecked = true

        toggleMonth.setOnCheckedChangeListener { _, isChecked -> // 각 보기별로 프래그먼트 화면 출력
            if (isChecked) {
                toggleWeek.isChecked = false
                val fragment = CalMonthFragment()
                replaceFragment(fragment, "CalMonth")
            }
        }
        toggleWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toggleMonth.isChecked = false
                replaceFragment(CalWeekFragment(), "CalWeek")
            }
        }
        addSchedule.setOnClickListener { // 일정 추가 dialog 호출
            val dialogFragment = CalScheduleAddFragment()
            val calMonthFragment = childFragmentManager.findFragmentByTag("CalMonth") as? CalMonthFragment
            val calWeekFragment = childFragmentManager.findFragmentByTag("CalWeek") as? CalWeekFragment
            val bundle = Bundle()

            val text: String    // 월간 화면이 띄워져있으면 월간 화면의 현재 날짜를 putString으로 보내고, 주간 화면이어도 동일.
            if (calMonthFragment != null && calMonthFragment.isVisible) {
                text = calMonthFragment.getCurrentDate()
            } else if (calWeekFragment != null && calWeekFragment.isVisible) {
                text = calWeekFragment.getCurrentDateWeek()
            } else {
                text = "error"
            }

            val convertText = convertDateFormat(text)
            bundle.putString("date", convertText) // 날짜 putString으로 dialogFragment에 보내기
            dialogFragment.arguments = bundle

            dialogFragment.show(parentFragmentManager, "openAddDialog") // 일정추가 dialog 호출
        }
    }

    private fun convertDateFormat(input: String): String { // 날짜 형식 변환 함수
        try {
            val inputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())

            val inputDate = inputFormat.parse(input)
            return outputFormat.format(inputDate)
        } catch (e: Exception) {
            e.printStackTrace()
            return input
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) { // 프래그먼트 교체 함수 (월간 <-> 주간)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.calFragmentContainerView, fragment, tag)
            .setReorderingAllowed(true)
            .commit()
    }
}

class CalMonthFragment : Fragment(R.layout.fragment_cal_month) {    // 월간 프래그먼트 클래스
    private lateinit var currentDate: TextView
    private lateinit var calMonthViewModel: CalMonthViewModel

    fun getCurrentDate(): String {  // 월간 프래그먼트의 현재 날짜 getter 함수 (일정 추가 dialog에 날짜 표시에 활용됨)
        return currentDate.text.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        currentDate = view.findViewById(R.id.txtCurrentDate)

        // 처음 생성시 오늘 날짜 기본 출력
        val today = Calendar.getInstance().time
        val todayDateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        val todayString = todayDateFormat.format(today)
        currentDate.text = todayString

        // viewmodel 호출. 일정 추가기능을 livedata로 구현. db연결 코드 대신 대체함.
        // 추후 db 연동이 되면 viewmodel클래스와 함께 삭제 예정.
        calMonthViewModel = ViewModelProvider(requireActivity())[CalMonthViewModel::class.java]
        val adapter = initScheduleList(view)
        var selectedDateToAdapter= ""

        val dateFormatToAdapter = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault()).format(today)
        updateViewModel(adapter, dateFormatToAdapter)

        // 날짜 누르면 날짜를 textview에 출력, 날짜에 맞는 일정 리스트에 표시
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            currentDate.text = selectedDate

            selectedDateToAdapter = "$year - ${String.format("%02d", month + 1)} - ${String.format("%02d", dayOfMonth)}"
            updateViewModel(adapter, selectedDateToAdapter)
        }

        adapter.setItemClickListener(object: ScheduleClickListener { // 일정 상세정보 dialog 호출
            override fun onItemClick(item: CalMonth) {
                val dialogFragment = CalScheduleInfoFragment()
                val bundle = Bundle()
                dialogFragment.setBottomSheetListener(object: BottomSheetListener {
                    override fun onDismissed() { // 삭제, 수정 요청시 recyclerview 항목 갱신 수행
                        adapter.updateList(selectedDateToAdapter)
                    }
                })

                bundle.putParcelable("scheduleInfo", item) // item의 정보를 bottomdialog로 넘기기
                dialogFragment.arguments = bundle
                dialogFragment.show(parentFragmentManager, "scheduleInfo")
            }
        })
    }

    private fun updateViewModel(adapter: CalMonthListAdapter, date: String) { // 아이템이 추가될 때마다 호출됨(실시간 데이터 변경 감지) -> db연결 후에는 서버 연결 코드로 변경 예정.
        calMonthViewModel.calMonthList.observe(viewLifecycleOwner) { calMonthList ->
            adapter.clearItem() // 업데이트 전 리스트 초기화 후 항목을 모두 추가 (중복 삽입 방지)
            calMonthList.forEach { calMonth ->
                adapter.addItem(calMonth)
            }
            adapter.updateList(date)
        }
    }

    private fun initScheduleList(v: View): CalMonthListAdapter { // 월간 일정 화면 뷰 초기화 함수
        val itemList = ArrayList<CalMonth>()
        val recyclerView = v.findViewById<RecyclerView>(R.id.calMonthScheduleView)
        val adapter = CalMonthListAdapter(itemList)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val divider = DividerItemDecoration(requireContext(), layoutManager.orientation)
        recyclerView.addItemDecoration(divider)

        return adapter
    }
}

class CalWeekFragment : Fragment(R.layout.fragment_cal_week) {
    private lateinit var datePickTxt: TextView
    private lateinit var txtCurrentDateWeek: TextView
    fun getCurrentDateWeek(): String {
        return txtCurrentDateWeek.text.toString()
    }

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

class CalScheduleAddFragment : BottomSheetDialogFragment(R.layout.dialog_schedule_add) {
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var txtCurrentDateAdd: TextView
    private lateinit var txtCurrentDateAddEnd: TextView
    private lateinit var calMonthViewModel: CalMonthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedDate = arguments?.getString("date") ?: ""

        startTime = view.findViewById(R.id.txtStartTime)
        endTime = view.findViewById(R.id.txtEndTime)
        txtCurrentDateAdd = view.findViewById(R.id.txtCurrentDateAdd)
        txtCurrentDateAddEnd = view.findViewById(R.id.txtCurrentDateAddEnd)

        val title = view.findViewById<EditText>(R.id.editTextAddTitle)
        val fullTime = view.findViewById<CheckBox>(R.id.isFullCheck)
        val info = view.findViewById<EditText>(R.id.editTxtSchInfoFix)

        fullTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startTime.visibility = View.GONE
                endTime.visibility = View.GONE
            } else {
                startTime.visibility = View.VISIBLE
                endTime.visibility = View.VISIBLE
            }
        }

        txtCurrentDateAdd.text = receivedDate
        txtCurrentDateAddEnd.text = receivedDate

        val dialogTitle = view.findViewById<TextView>(R.id.txtTitleAddScheduleModify) // 다이얼로그 타이틀
        val add = view.findViewById<TextView>(R.id.btnAddScheduleDialogModify) // 추가버튼(추가, 수정 동시기능)
        val cancel = view.findViewById<TextView>(R.id.btnCancelScheduleDialog) // 취소버튼

        val modifiedData = arguments?.getParcelable<CalMonth>("scheduleFix")
        if (modifiedData != null) {          // 수정버튼을 호출한 경우 데이터 수정이 이루어지는 코드 작성
            dialogTitle.text = "일정 수정"      // 제목을 "일정 추가" -> "일정 수정"으로 교체
            add.text = "수정"                  // 버튼텍스트를 추가에서 수정으로 교체
            fullTime.isChecked = modifiedData.isFullTime // "종일" 항목을 체크하였는지 여부 설정

            title.setText(modifiedData.title.toString()) // 수정시킬 데이터를 불러오는 부분
            info.setText(modifiedData.info)

            // 시작시간, 끝시간 날짜 시간부분 분리하여 각각의 textview에 저장
            splitDateTime(modifiedData.startTime.toString(), "start", modifiedData.isFullTime)
            splitDateTime(modifiedData.endTime.toString(), "end", modifiedData.isFullTime)
        }

        startTime.setOnClickListener {
            showTimePickDialog("start")
        }
        endTime.setOnClickListener {
            showTimePickDialog("end")
        }
        txtCurrentDateAdd.setOnClickListener {
            val currentDate = txtCurrentDateAdd.text.toString()
            showDatePickDialog("start", currentDate)
        }
        txtCurrentDateAddEnd.setOnClickListener {
            val currentDate = txtCurrentDateAddEnd.text.toString()
            showDatePickDialog("end", currentDate)
        }

        calMonthViewModel = ViewModelProvider(requireActivity())[CalMonthViewModel::class.java]

        add.setOnClickListener {
            val data = bringCurrentData(title, info, fullTime)

            if (title.text.toString().isEmpty()) { // 제목 미 입력시 입력하도록 설계
                Toast.makeText(requireContext(), "재목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                if (add.text.equals("추가")) {
                    calMonthViewModel.addDateMonth(data)
                    Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else if (add.text.equals("수정")) {
                    calMonthViewModel.deleteDateMonth(modifiedData!!)
                    calMonthViewModel.addDateMonth(data)
                    Toast.makeText(requireContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                }

                dismiss()
            }
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }
    // 작성한 데이터를 리스트에 등록하기 위해 데이터를 리턴하는 함수
    private fun bringCurrentData(title: EditText, info: EditText, fullTime: CheckBox): CalMonth {
        val currentDateStart = txtCurrentDateAdd.text.toString() // 위와 변수를 중복 선언한 이유는 값을 바로 가져 와야 하기 때문(animator 실습때와 동일)
        val currentDateEnd = txtCurrentDateAddEnd.text.toString()
        val data: CalMonth

        if (fullTime.isChecked) { // 종일이 체크되어있으면 시간대는 "종일"로 기록, 아니면 시간대를 저장
            data = CalMonth(title.text.toString(), "${currentDateStart} 종일", "${currentDateEnd} 종일",
                info.text.toString(), fullTime.isChecked)
        }
        else data = CalMonth(title.text.toString(), "${currentDateStart} ${startTime.text}",
            "${currentDateEnd} ${endTime.text}", info.text.toString(), fullTime.isChecked)

        return data
    }

    private fun splitDateTime(dateTime: String, status: String, isChecked: Boolean) { // (수정모드에서만 호출되는 함수)
        val date = dateTime.take(14)
        val time = dateTime.takeLast(8)

        if (!isChecked) {
            when (status) {
                "start" -> {
                    txtCurrentDateAdd.text = date
                    startTime.text = time
                }
                "end" -> {
                    txtCurrentDateAddEnd.text = date
                    endTime.text = time
                }
            }
        } else {
            when (status) {
                "start" -> txtCurrentDateAdd.text = date
                "end" -> txtCurrentDateAddEnd.text = date
            }
        }
    }

    private fun showTimePickDialog(status: String) { // 시간 선택 다이얼로그 호출 함수
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_time_pick, null)

        val noon = view.findViewById<NumberPicker>(R.id.pickNoon) // 오전/오후 선택
        val hour = view.findViewById<NumberPicker>(R.id.pickHour) // 시간 선택
        val minute = view.findViewById<NumberPicker>(R.id.pickMinute) // 분 선택

        noon.minValue = 0; noon.maxValue = 1
        hour.minValue = 1; hour.maxValue = 12
        minute.minValue = 0; minute.maxValue = 59

        noon.displayedValues = arrayOf("오전", "오후")
        hour.setFormatter { String.format("%02d", it) }
        minute.setFormatter { String.format("%02d", it) }

        noon.wrapSelectorWheel = false // 오전/오후 선택 부분만 순환하지 않도록 설정

        builder.setView(view).setTitle("시간 선택")
            .setPositiveButton("OK") { dialog, _ ->
                val timeValue = timeValue(noon.value, hour.value, minute.value)

                if (status.equals("start")) {
                    autoSetEndTime(timeValue, endTime.text.toString())
                    startTime.text = timeValue
                }
                else if (status.equals("end")) {
                    if (checkIfReversedTime(startTime.text.toString(), timeValue))
                        endTime.text = timeValue
                }
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDatePickDialog(status: String, dateString: String) { // 날짜 선택 다이얼로그 창 출력 함수
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_schedule_datepick, null)

        val calendar = view.findViewById<CalendarView>(R.id.calendarViewDialog)
        selectDateOnInit(calendar, dateString) // 선택한 날짜로 미리 선택해놓도록 설정

        var selectedDate = Date()
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calInstance = Calendar.getInstance()
            calInstance.set(year, month, dayOfMonth)
            selectedDate = calInstance.time
        }

        builder.setView(view).setTitle("날짜 선택")
            .setPositiveButton("OK") { dialog, _ ->
                selectDateOnChoose(status, selectedDate)
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun selectDateOnInit(calendar: CalendarView, dateString: String) { // 날짜 다이얼로그가 호출될때 현재 날짜를 미리 선택하도록 설정하는 함수
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString.replace(" ", "")) ?: Date()

        val calInstance = Calendar.getInstance()
        calInstance.time = date

        calendar.date = calInstance.timeInMillis
    }

    private fun selectDateOnChoose(status: String, selectedDate: Date) { // 날짜를 선택하고 ok를 누를때 날짜를 textview에 넘기는 함수
        val dateFormat = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)

        if (status.equals("start")) {
            if (checkIfReversed(formattedDate, txtCurrentDateAddEnd.text.toString(), status))
                txtCurrentDateAdd.text = formattedDate
        }
        else if (status.equals("end")) {
            if (checkIfReversed(txtCurrentDateAdd.text.toString(), formattedDate, status))
                txtCurrentDateAddEnd.text = formattedDate
        }
    }

    private fun timeValue(noon: Int, hour: Int, minute: Int): String { // dialog 선택후 textview에 출력될 테스트 반환 함수
        val isNoon: String
        if (noon == 0) isNoon = "오전"
        else isNoon = "오후"

        val timeText = isNoon + " ${String.format("%02d", hour)}:${String.format("%02d", minute)}"

        return timeText
    }

    private fun checkIfReversed(start: String, end: String, status: String): Boolean { // 시작날짜와 끝날짜가 서로가 서로보다 앞서거나 뒤서면 감지하는 함수
        val first = start.replace(" - ", "").toInt()
        val second = end.replace(" - ", "").toInt()
        val (startT, endT) = timeTakeLast(startTime.text.toString(), endTime.text.toString())

        if (first > second || (first == second && startT > endT)) {
            if (status.equals("start")) {
                txtCurrentDateAdd.text = start // 끝날짜를 시작날짜와 같게 설정. (날짜선택을 제한적으로 하지 않게 하려는 배려)
                txtCurrentDateAddEnd.text = start
            }
            else if (status.equals("end")) {
                Toast.makeText(requireContext(), "날짜 설정 오류입니다.", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        else return true
    }

    private fun getStartAndEndDates(): Pair<String, String> {
        return Pair(txtCurrentDateAdd.text.toString(), txtCurrentDateAddEnd.text.toString())
    }

    private fun autoSetEndTime(start: String, end: String) { // 시작 시간이 끝 시간보다 나중으로 설정하면 자동으로 끝 시간을 시작 시간의 정확히 한 시간 뒤로 설정하는 함수
        val (startDate, endDate) = getStartAndEndDates()
        val (first, second) = timeTakeLast(start, end)
        println("-- startDate: " + startDate + " endDate: " + endDate)

        if (startDate.equals(endDate) && (first > second)) { // 날짜가 같고 시작시간을 더 나중으로 설정하였을 때
            val endChange = start
            val formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
            val time = LocalTime.parse(endChange, formatter)

            // 시간을 1 증가시키고 다시 문자열로 변환
            val incrementedTime = time.plusHours(1)
            val resultEndTime = incrementedTime.format(formatter)

            endTime.text = resultEndTime

            if (resultEndTime.contains("오전 12:")) { // 자동설정된 끝 시간이 오전 12시 이상 (다음날로 넘어가는 경우) 긑날짜 1일 증가.
                val regex = Regex("(\\d{4} - \\d{2} - \\d{2})")
                val text = txtCurrentDateAddEnd.text.toString()

                val resultDateEnd = text.replace(regex) {
                    val date = it.groupValues[1]

                    // LocalDate로 파싱
                    val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy - MM - dd"))

                    // 날짜의 일 부분을 1 증가시키고 문자열로 변환
                    val modifiedDate = parsedDate.plusDays(1)
                    modifiedDate.format(DateTimeFormatter.ofPattern("yyyy - MM - dd"))
                }
                txtCurrentDateAddEnd.text = resultDateEnd
            }
        }
    }

    private fun checkIfReversedTime(start: String, end: String): Boolean { // 선택한 날짜가 같은 경우 끝시간에 시작시간보다 먼저면 자동으로 설정하는 함수
        val (startDate, endDate) = getStartAndEndDates()
        val (first, second) = timeTakeLast(start, end)

        if (startDate.equals(endDate) && (first > second)) {
            Toast.makeText(requireContext(), "시간 설정 오류입니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        else return true
    }

    private fun timeTakeLast(start: String, end: String): Pair<Int, Int> { // 시간을 숫자로 변환하는 함수(시간 연산을 위해 작성)
        var first = start.takeLast(5).replace(":", "").toInt()
        var second = end.takeLast(5).replace(":", "").toInt()

        if (start.contains("오후")) { // 오전 12시 = 00시, 오후12시 = 12시 로직 추가. 오후는 설정한 시간에서 12시간 더하도록 설정.
            if (!start.contains("오후 12:"))
                first += 1200
        }
        else if (start.contains("오전 12:")) {
            first -= 1200
        }

        if (end.contains("오후")) {
            if (!end.contains("오후 12:"))
                second += 1200
        }
        else if (end.contains("오전 12:")) {
            second -= 1200
        }

        println("first: " + first + "second: " + second)
        return Pair(first, second)
    }
}

interface BottomSheetListener {
    fun onDismissed()
}
// 일정 상세 화면 다이얼로그
class CalScheduleInfoFragment : BottomSheetDialogFragment(R.layout.dialog_schedule_info) {
    private lateinit var calMonthViewModel: CalMonthViewModel
    private var dialogListener: BottomSheetListener? = null

    fun setBottomSheetListener(listener: BottomSheetListener) {
        dialogListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val delete = view.findViewById<TextView>(R.id.btnSchDeleteInfo)
        val modify = view.findViewById<TextView>(R.id.btnSchModifyInfo)
        val item = arguments?.getParcelable<CalMonth>("scheduleInfo")
        calMonthViewModel = ViewModelProvider(requireActivity())[CalMonthViewModel::class.java]

        showInfo(view, item)

        delete.setOnClickListener {
            deleteSchedule(item)
            dismiss()
        }
        modify.setOnClickListener {
//            val newItem = CalMonth("수정제목","수정시작시간", "수정끝시간", "수정정보",false)
            modifySchedule(item)

            dismiss()
        }
    }

    private fun showInfo(v: View, item: CalMonth?) { // 일정 정보 출력 함수
        if (item == null) return // null값 예외 처리
        else {
            val title = v.findViewById<TextView>(R.id.txtSchTitleInfo)
            val period = v.findViewById<TextView>(R.id.txtSchPeriodInfo)
            val periodEnd = v.findViewById<TextView>(R.id.txtSchPeriodInfoEnd)
            val description = v.findViewById<TextView>(R.id.txtSchDesInfo)

            title.text = item.title
            period.text = item.startTime
            periodEnd.text = item.endTime
            description.text = item.info
        }
    }

    private fun deleteSchedule(selectedItem: CalMonth?) { // 일정 삭제 함수
        if (selectedItem == null) return
        else {
            calMonthViewModel.deleteDateMonth(selectedItem)
            dialogListener?.onDismissed()
            Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun modifySchedule(item: CalMonth?) { // 일정 수정 함수
        if (item == null) return
        else {
//            calMonthViewModel.modifyDateMonth(selectedItem, newItem)
//            dialogListener?.onDismissed("modify")
            val fixDialog = CalScheduleAddFragment()
            val bundle = Bundle()

            bundle.putParcelable("scheduleFix", item) // item의 정보를 bottomdialog로 넘기기
            fixDialog.arguments = bundle
            fixDialog.show(parentFragmentManager, "fixDialog")
        }
    }
}

