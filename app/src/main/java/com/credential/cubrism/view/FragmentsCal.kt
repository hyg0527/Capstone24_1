package com.credential.cubrism.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.R
import com.credential.cubrism.databinding.DialogScheduleDatepickBinding
import com.credential.cubrism.databinding.DialogTimePickBinding
import com.credential.cubrism.databinding.FragmentCalBinding
import com.credential.cubrism.view.adapter.CalListAdapter
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.CalendarAdapter
import com.credential.cubrism.view.adapter.DateMonthClickListener
import com.credential.cubrism.view.adapter.DateSelect
import com.credential.cubrism.view.adapter.ScheduleClickListener
import com.credential.cubrism.viewmodel.CalMonthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalFragment : Fragment(R.layout.fragment_cal) {
    private var _binding: FragmentCalBinding? = null
    private val binding get() = _binding!!
    private lateinit var calMonthViewModel: CalMonthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calHyg = CalendarHyg()
        // viewmodel 호출. 일정 추가기능을 livedata로 구현.
        calMonthViewModel = ViewModelProvider(requireActivity())[CalMonthViewModel::class.java]
        val adapter = initScheduleList()

        val calendarAdapter = CalendarAdapter(ArrayList()) // 날짜 어댑
        val layoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRealView.layoutManager = layoutManager // 월간 달력 recyclerView 초기화
        binding.calendarRealView.adapter = calendarAdapter

        calHyg.initToday(binding.txtYearMonth, binding.currentDate, calendarAdapter, calMonthViewModel.calMonthList.value) { date ->
            updateViewModel(adapter, date)
        }

        val addFragment = CalScheduleAddFragment()
        addFragment.setAddListener(object: AddDot {
            override fun onAddDelete() {
                val pickTxt = binding.txtYearMonth.text.toString()
                val (days, weekIndex) = calHyg.getMonthInfoS(pickTxt)

                val monthUpdateList = calHyg.showMonthCalendar(pickTxt, days, weekIndex, calMonthViewModel.calMonthList.value)
                calendarAdapter.updateCalendar(monthUpdateList)
                calendarAdapter.highlightDate(convertDateFormat(binding.currentDate.text.toString()))
            }
            override fun bringInfo(item: CalMonth?) {}
        })

        val infoFragment = CalScheduleInfoFragment()
        infoFragment.setAddListener(object: AddDot {
            override fun onAddDelete() {
                val pickTxt = binding.txtYearMonth.text.toString()
                val (days, weekIndex) = calHyg.getMonthInfoS(pickTxt)

                val monthUpdateList = calHyg.showMonthCalendar(pickTxt, days, weekIndex, calMonthViewModel.calMonthList.value)
                calendarAdapter.updateCalendar(monthUpdateList)
                calendarAdapter.highlightDate(convertDateFormat(binding.currentDate.text.toString()))
            }
            override fun bringInfo(item: CalMonth?) {
                val bundle = Bundle()
                bundle.putParcelable("scheduleFix", item) // 날짜 putString으로 dialogFragment에 보내기

                addFragment.arguments = bundle
                addFragment.show(parentFragmentManager, "fixDialog") // 일정추가 dialog 호출
            }
        })

        binding.btnAddSchedule.setOnClickListener { // 일정 추가 dialog 호출
            val bundle = Bundle()
            val text = getCurrentDate() // 월간 화면이 띄워져있으면 월간 화면의 현재 날짜를 putString으로 보내기
            val convertText = convertDateFormat(text)

            bundle.putString("date", convertText) // 날짜 putString으로 dialogFragment에 보내기
            addFragment.arguments = bundle
            addFragment.show(parentFragmentManager, "openAddDialog") // 일정추가 dialog 호출
        }

        binding.dateSelect.setOnClickListener {
            showDatePickDialog(calHyg, calendarAdapter, calMonthViewModel.calMonthList.value) { date ->
                updateViewModel(adapter, date)
            }
        }
        binding.preMonth.setOnClickListener {
            calHyg.setPreNextMonthCalendar(calendarAdapter, binding.txtYearMonth, binding.currentDate,
                calMonthViewModel.calMonthList.value, "pre") { date ->
                updateViewModel(adapter, date)
                calendarAdapter.highlightDate(date)
            }
        }
        binding.nextMonth.setOnClickListener {
            calHyg.setPreNextMonthCalendar(calendarAdapter, binding.txtYearMonth, binding.currentDate,
                calMonthViewModel.calMonthList.value, "next") { date ->
                updateViewModel(adapter, date)
                calendarAdapter.highlightDate(date)
            }
        }
        binding.btnToday.setOnClickListener {
            calHyg.initToday(binding.txtYearMonth, binding.currentDate, calendarAdapter,
                calMonthViewModel.calMonthList.value) { date ->
                updateViewModel(adapter, date)
                calendarAdapter.highlightDate(date)
            }
        }
        binding.btnAddSchedule.setOnClickListener { // 일정 추가 dialog 호출
            val bundle = Bundle()
            val text = getCurrentDate()
            val convertText = convertDateFormat(text)

            bundle.putString("date", convertText) // 날짜 putString으로 dialogFragment에 보내기
            addFragment.arguments = bundle
            addFragment.show(parentFragmentManager, "openAddDialog") // 일정 추가 dialog 호출
        }

        calendarAdapter.setItemClickListener(object: DateMonthClickListener {
            override fun onItemClicked(item: DateSelect) {
                if (item.date!!.isDigitsOnly()) {
                    val day = item.date
                    val regex = Regex("\\d+일")
                    val text = binding.currentDate.text.toString().replace(regex, "${day}일")
                    // 달력의 날짜 누르면 textview 날짜 갱신
                    binding.currentDate.text = text
                    calendarAdapter.highlightCurrentDate(item, true)

                    val intRegex = """(\d{4})년 (\d{1,2})월 (\d{1,2})일""".toRegex()
                    intRegex.find(text)?.let {
                        val (foundYear, foundMonth, foundDay) = it.destructured
                        val dateSelected = "${foundYear.toInt()} - ${String.format("%02d", foundMonth.toInt())} - ${String.format("%02d", foundDay.toInt())}"

                        updateViewModel(adapter, dateSelected)
                    }
                }
            }
        })

        adapter.setItemClickListener(object: ScheduleClickListener { // 일정 상세정보 dialog 호출
            override fun onItemClick(item: CalMonth) {
                val bundle = Bundle()
                bundle.putParcelable("scheduleInfo", item) // item의 정보를 bottomdialog로 넘기기

                infoFragment.arguments = bundle
                infoFragment.show(parentFragmentManager, "scheduleInfo")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentDate(): String {  // 월간 프래그먼트의 현재 날짜 getter 함수 (일정 추가 dialog에 날짜 표시에 활용됨)
        return binding.currentDate.text.toString()
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

    private fun showDatePickDialog(calInstance: CalendarHyg, calAdapter: CalendarAdapter,
                                   data: ArrayList<CalMonth>? = null, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_date_pick, null)

        calInstance.showDatePickDialog(view, builder, binding.txtYearMonth, binding.currentDate, calAdapter, data) { selectedDate ->
            callback(selectedDate)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun updateViewModel(adapter: CalListAdapter, date: String) { // 아이템이 추가될 때마다 호출됨(실시간 데이터 변경 감지)
        calMonthViewModel.calMonthList.observe(viewLifecycleOwner) { calList ->
            adapter.clearItem() // 업데이트 전 리스트 초기화 후 항목을 모두 추가 (중복 삽입 방지)

            calList.forEach { calMonth ->
                adapter.addItem(calMonth)
            }
            adapter.updateList(date)
        }
    }

    private fun initScheduleList(): CalListAdapter { // 일정 리스트 초기화 함수
        val itemList = ArrayList<CalMonth>()
        val recyclerView = binding.calMonthScheduleView
        val adapter = CalListAdapter(itemList)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        return adapter
    }
}

interface AddDot {
    fun onAddDelete()
    fun bringInfo(item: CalMonth?)
}
class CalScheduleAddFragment : BottomSheetDialogFragment(R.layout.dialog_schedule_add) {
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var txtCurrentDateAdd: TextView
    private lateinit var txtCurrentDateAddEnd: TextView
    private lateinit var calMonthViewModel: CalMonthViewModel
    private var listener: AddDot? = null

    fun setAddListener(listener: AddDot) {
        this.listener = listener
    }

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
        title.setText(""); info.setText("")

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

        val modifiedData = arguments?.getParcelable<CalMonth>("scheduleFix")
        if (modifiedData != null) {          // 수정버튼을 호출한 경우 데이터 수정이 이루어지는 코드 작성
            println("modified data: $modifiedData")
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
                if (add.text.equals("+")) {
                    calMonthViewModel.addDateMonth(data)
                    println(calMonthViewModel.calMonthList.value)
                    Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    listener?.onAddDelete()
//                    title.setText(""); info.setText(""); fullTime.isChecked = false
                }
                else if (add.text.equals("수정")) {
                    calMonthViewModel.deleteDateMonth(modifiedData!!)
                    calMonthViewModel.addDateMonth(data)
                    Toast.makeText(requireContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    listener?.onAddDelete()
//                    title.setText(""); info.setText(""); fullTime.isChecked = false
                }
                else {
                    Toast.makeText(requireContext(), "일정 추가/수정 오류", Toast.LENGTH_SHORT).show()
                }

                dismiss()
            }
        }
    }

    // 작성한 데이터를 리스트에 등록하기 위해 데이터를 리턴하는 함수
    private fun bringCurrentData(title: EditText, info: EditText, fullTime: CheckBox): CalMonth {
        val currentDateStart = txtCurrentDateAdd.text.toString() // 위와 변수를 중복 선언한 이유는 값을 바로 가져 와야 하기 때문(animator 실습때와 동일)
        val currentDateEnd = txtCurrentDateAddEnd.text.toString()
        val data: CalMonth

        if (fullTime.isChecked) { // 종일이 체크되어 있으면 시간대는 "종일"로 기록, 아니면 시간대를 저장
            data = CalMonth(title.text.toString(), "$currentDateStart 종일", "$currentDateEnd 종일",
                info.text.toString(), fullTime.isChecked)
        }
        else data = CalMonth(title.text.toString(), "$currentDateStart ${startTime.text}",
            "$currentDateEnd ${endTime.text}", info.text.toString(), fullTime.isChecked)

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
        val inflater = requireActivity().layoutInflater

        val binding = DialogTimePickBinding.inflate(inflater)

        val noon = binding.pickNoon // 오전/오후 선택
        val hour = binding.pickHour // 시간 선택
        val minute = binding.pickMinute // 분 선택

        noon.minValue = 0; noon.maxValue = 1
        hour.minValue = 1; hour.maxValue = 12
        minute.minValue = 0; minute.maxValue = 59

        noon.displayedValues = arrayOf("오전", "오후")
        hour.setFormatter { String.format("%02d", it) }
        minute.setFormatter { String.format("%02d", it) }
        noon.wrapSelectorWheel = false // 오전/오후 선택 부분만 순환 하지 않도록 설정

        builder.setTitle("시간 선택")
            .setPositiveButton("OK") { dialog, _ ->
                val timeValue = timeValue(noon.value, hour.value, minute.value)

                if (status == "start") {
                    autoSetEndTime(timeValue, endTime.text.toString())
                    startTime.text = timeValue
                }
                else if (status == "end") {
                    if (checkIfReversedTime(startTime.text.toString(), timeValue))
                        endTime.text = timeValue
                }
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDatePickDialog(status: String, dateString: String) { // 날짜 선택 다이얼로그 창 출력 함수
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val binding = DialogScheduleDatepickBinding.inflate(inflater)

        val calendar = binding.calendarViewDialog
        selectDateOnInit(calendar, dateString) // 선택한 날짜로 미리 선택해놓도록 설정

        var selectedDate = Date()
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calInstance = Calendar.getInstance()
            calInstance.set(year, month, dayOfMonth)
            selectedDate = calInstance.time
        }

        builder.setTitle("날짜 선택")
            .setPositiveButton("OK") { dialog, _ ->
                selectDateOnChoose(status, selectedDate)
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.show()
    }

    private fun selectDateOnInit(calendar: CalendarView, dateString: String) { // 날짜 다이얼로그가 호출될 때 현재 날짜를 미리 선택하도록 설정하는 함수
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString.replace(" ", "")) ?: Date()

        val calInstance = Calendar.getInstance()
        calInstance.time = date

        calendar.date = calInstance.timeInMillis
    }

    private fun selectDateOnChoose(status: String, selectedDate: Date) { // 날짜를 선택 하고 ok를 누를때 날짜를 textview에 넘기는 함수
        val dateFormat = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)

        if (status == "start") {
            if (checkIfReversed(formattedDate, txtCurrentDateAddEnd.text.toString(), status))
                txtCurrentDateAdd.text = formattedDate
        }
        else if (status == "end") {
            if (checkIfReversed(txtCurrentDateAdd.text.toString(), formattedDate, status))
                txtCurrentDateAddEnd.text = formattedDate
        }
    }

    private fun timeValue(noon: Int, hour: Int, minute: Int): String { // dialog 선택후 textview에 출력될 테스트 반환 함수
        val isNoon = if (noon == 0) "오전" else "오후"
        return "$isNoon ${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    private fun checkIfReversed(start: String, end: String, status: String): Boolean { // 시작날짜와 끝날짜가 서로가 서로보다 앞서거나 뒤서면 감지하는 함수
        val first = start.replace(" - ", "").toInt()
        val second = end.replace(" - ", "").toInt()
        val (startT, endT) = timeTakeLast(startTime.text.toString(), endTime.text.toString())

        return if (first > second || (first == second && startT > endT)) {
            if (status == "start") {
                txtCurrentDateAdd.text = start // 끝날짜를 시작날짜와 같게 설정. (날짜선택을 제한적으로 하지 않게 하려는 배려)
                txtCurrentDateAddEnd.text = start
            }
            else if (status == "end") {
                Toast.makeText(requireContext(), "날짜 설정 오류입니다.", Toast.LENGTH_SHORT).show()
            }
            false
        }
        else true
    }

    private fun getStartAndEndDates(): Pair<String, String> {
        return Pair(txtCurrentDateAdd.text.toString(), txtCurrentDateAddEnd.text.toString())
    }

    private fun autoSetEndTime(start: String, end: String) { // 시작 시간이 끝 시간 보다 나중 으로 설정 하면 자동 으로 끝 시간을 시작 시간의 정확히 한 시간 뒤로 설정하는 함수
        val (startDate, endDate) = getStartAndEndDates()
        val (first, second) = timeTakeLast(start, end)

        if (startDate == endDate && (first > second)) { // 날짜가 같고 시작 시간을 더 나중 으로 설정 하였을 때
            val formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
            val time = LocalTime.parse(start, formatter)

            // 시간을 1 증가 시키고 다시 문자열 로 변환
            val incrementedTime = time.plusHours(1)
            val resultEndTime = incrementedTime.format(formatter)

            endTime.text = resultEndTime

            if (resultEndTime.contains("오전 12:")) { // 자동 설정된 끝 시간이 오전 12시 이상 (다음 날로 넘어 가는 경우) 긑날짜 1일 증가.
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

        return if (startDate == endDate && (first > second)) {
            Toast.makeText(requireContext(), "시간 설정 오류입니다.", Toast.LENGTH_SHORT).show()
            false
        }
        else true
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

// 일정 상세 화면 다이얼로그
class CalScheduleInfoFragment : BottomSheetDialogFragment(R.layout.dialog_schedule_info) {
    private lateinit var calMonthViewModel: CalMonthViewModel
    private lateinit var start: TextView
    private lateinit var end: TextView
    private var listener: AddDot? = null

    fun setAddListener(listener: AddDot) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val delete = view.findViewById<ImageView>(R.id.btnSchDeleteInfo)
        val modify = view.findViewById<ImageView>(R.id.btnSchModifyInfo)
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
            start = v.findViewById(R.id.txtSchPeriodInfo)
            end = v.findViewById(R.id.txtSchPeriodInfoEnd)
            val description = v.findViewById<TextView>(R.id.txtSchDesInfo)

            title.text = item.title
            start.text = item.startTime
            end.text = item.endTime
            description.text = item.info
        }
    }

    private fun deleteSchedule(selectedItem: CalMonth?) { // 일정 삭제 함수
        if (selectedItem == null) return
        else {
            calMonthViewModel.deleteDateMonth(selectedItem)
            listener?.onAddDelete()
            Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun modifySchedule(item: CalMonth?) { // 일정 수정 함수
        if (item == null) return
        else {
            listener?.bringInfo(item)
        }
    }
}

