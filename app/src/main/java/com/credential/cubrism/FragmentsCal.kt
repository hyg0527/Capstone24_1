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
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
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
    private lateinit var monthPickTxt: TextView
    private lateinit var calMonthViewModel: CalMonthViewModel

    fun getCurrentDate(): String {  // 월간 프래그먼트의 현재 날짜 getter 함수 (일정 추가 dialog에 날짜 표시에 활용됨)
        return currentDate.text.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentDate = view.findViewById(R.id.txtCurrentDate)
        monthPickTxt = view.findViewById(R.id.txtYearMonth2)

        // 처음 생성시 오늘 날짜 기본 출력
        val today = Calendar.getInstance().time
        val todayDateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        val todayString = todayDateFormat.format(today)
        currentDate.text = todayString

        // viewmodel 호출. 일정 추가기능을 livedata로 구현. db연결 코드 대신 대체함.
        calMonthViewModel = ViewModelProvider(requireActivity())[CalMonthViewModel::class.java]
        val adapter = initScheduleList(view)
        var selectedDateToAdapter= ""

        val calHyg = CalendarHyg()
        val calInstance = calHyg.getInstance() // 현재 날짜에 대한 일수, 요일 정보 추출
        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
        calInstance.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeekIndex = calInstance.get(Calendar.DAY_OF_WEEK)
        val monthList = calHyg.showMonthCalendar(daysInMonth, dayOfWeekIndex) // 해당 연월에 대한 달력 출력 함수

        val (year, month) = calHyg.getYearMonth() // 상단의 연월 출력 부분
        val monthString = calHyg.selectedMonthToString(month)
        val currentMonthYear = "$monthString $year"
        monthPickTxt.text = currentMonthYear

        val calendarRealView = view.findViewById<RecyclerView>(R.id.calendarRealView) // 월간달력 recyclerView 초기화
        val calMonthAdapter = CalendarAdapter(monthList) // 날짜 어댑터

        val layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRealView.layoutManager = layoutManager
        calendarRealView.adapter = calMonthAdapter

        val dateFormatToAdapter = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault()).format(today)
        updateViewModel(adapter, dateFormatToAdapter)

        monthPickTxt.setOnClickListener {
            showDatePickDialogMonth(calHyg, calMonthAdapter) { date ->
                updateViewModel(adapter, date)
            }
        }

        calMonthAdapter.setItemClickListener(object: DateMonthClickListener {
            override fun onItemClicked(item: DateSelect) { // week랑 코드 중복인데 나중에 함수화 요망. (2번째부터 5줄)
                if (item.date!!.isDigitsOnly()) {
                    val day = item.date
                    val regex = Regex("\\d+일")
                    val text = currentDate.text.toString().replace(regex, "${day}일")
                    // 달력의 날짜 누르면 textview 날짜 갱신
                    currentDate.text = text

                    calMonthAdapter.highlightCurrentDate(item, true)
//                    calMonthAdapter.updateScheduleDot(item, true) // 날짜 누르면 하이라이팅 표시(임시)

                    val intRegex = """(\d{4})년 (\d{1,2})월 (\d{1,2})일""".toRegex()
                    intRegex.find(text)?.let {
                        val (year, month, day) = it.destructured
                        val dateSelected = "${year.toInt()} - ${String.format("%02d", month.toInt())} - ${String.format("%02d", day.toInt())}"

                        updateViewModel(adapter, dateSelected)
                    }
                }
            }
        })

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

    private fun showDatePickDialogMonth(calInstance: CalendarHyg, calMonthAdapter: CalendarAdapter, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_date_pick, null)

        calInstance.showDatePickDialog(view, builder, monthPickTxt, currentDate, calMonthAdapter) { selectedDate ->
            callback(selectedDate)
        }

        val dialog = builder.create()
        dialog.show()
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

    private fun initScheduleList(v: View): CalMonthListAdapter { // 일정 리스트 초기화 함수
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

        val calHyg = CalendarHyg()

        val (year, month) = calHyg.getYearMonth() // 현재 날짜정보를 가져와 년월 textview에 형식에 맞게 출력
        val monthString = calHyg.selectedMonthToString(month)
        val currentMonthYear = "$monthString $year"
        datePickTxt.text = currentMonthYear

        val calInstance = calHyg.getInstance() // 현재 날짜에 대한 일수, 요일 정보 추출
        val daysInMonth = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
        calInstance.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeekIndex = calInstance.get(Calendar.DAY_OF_WEEK)
        val weekList = calHyg.showWeekCalendar(daysInMonth, dayOfWeekIndex)
        val currentDate = calHyg.initToday()
        txtCurrentDateWeek.text = currentDate

        val calWeekView = view.findViewById<RecyclerView>(R.id.calWeekView) // 주간달력 recyclerView 초기화
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
            showDatePickDialogWeek(calHyg, calWeekAdapter)
        }

        val returnToday = view.findViewById<TextView>(R.id.txtTodayBtn)
        returnToday.setOnClickListener {  // today버튼 누르면 오늘로 돌아옴.
            val current = calHyg.initToday()
            // 연월과 달력도 같이 돌아오는 부분 추가
            datePickTxt.text = "$monthString $year"
            txtCurrentDateWeek.text = current
            val changedCalendar = calHyg.showWeekCalendar(daysInMonth, dayOfWeekIndex)
            calWeekAdapter.updateCalendar(changedCalendar)
        }
    }

    private fun showDatePickDialogWeek(calInstance: CalendarHyg, calMonthAdapter: CalWeekAdapter) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_date_pick, null)
        calInstance.showDatePickDialog(view, builder, datePickTxt, txtCurrentDateWeek, calMonthAdapter) { _ ->
        }

        val dialog = builder.create()
        dialog.show()
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
                Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
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
            val fixDialog = CalScheduleAddFragment()
            val bundle = Bundle()

            bundle.putParcelable("scheduleFix", item) // item의 정보를 bottomdialog로 넘기기
            fixDialog.arguments = bundle
            fixDialog.show(parentFragmentManager, "fixDialog")
        }
    }
}

