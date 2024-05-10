package com.credential.cubrism.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.R
import com.credential.cubrism.databinding.DialogScheduleAddBinding
import com.credential.cubrism.databinding.DialogScheduleDatepickBinding
import com.credential.cubrism.databinding.DialogScheduleInfoBinding
import com.credential.cubrism.databinding.DialogTimePickBinding
import com.credential.cubrism.databinding.FragmentCalBinding
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.model.repository.ScheduleRepository
import com.credential.cubrism.view.adapter.CalendarAdapter
import com.credential.cubrism.view.adapter.DateMonthClickListener
import com.credential.cubrism.view.adapter.DateSelect
import com.credential.cubrism.view.adapter.ScheduleAdapter
import com.credential.cubrism.viewmodel.ScheduleViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalFragment : Fragment() {
    private var _binding: FragmentCalBinding? = null
    private val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by viewModels { ViewModelFactory(ScheduleRepository()) }
    private val scheduleAdapter = ScheduleAdapter()

    private val calHyg = CalendarHyg()
    private var selectedDate: Triple<Int, Int, Int>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCalendarSchedule()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        observeViewModel(scheduleAdapter)

        val (year, month, day) = calHyg.initToday(binding.txtYearMonth, binding.currentDate)
        selectedDate = Triple(year, month, day)
        scheduleViewModel.getScheduleList(year, month)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initCalendarSchedule() {
        initScheduleList()

        val calendarAdapter = CalendarAdapter(ArrayList()) // 날짜 어댑터
        binding.calendarRealView.layoutManager = GridLayoutManager(requireContext(), 7) // 월간 달력 recyclerView 초기화
        binding.calendarRealView.adapter = calendarAdapter

        scheduleViewModel.scheduleList.observe(viewLifecycleOwner) {
//            for (list in scheduleAdapter.getItemList()) {
//                println(list)
//            }
            selectedDate?.let { (year, month, day) ->
                calHyg.showMonthCalendarNew(year, month, day, scheduleAdapter.getItemList(), calendarAdapter)
            }
            selectedDate = null
        }

        val (initYear, initMonth, initDay) = calHyg.initToday(binding.txtYearMonth, binding.currentDate)
        selectedDate = Triple(initYear, initMonth, initDay)
        scheduleViewModel.getScheduleList(initYear, initMonth)

        val infoFragment = CalScheduleInfoFragment()
        infoFragment.setAddListener(object: AddDot {
            override fun onAddDelete(item: ScheduleDto?) {
                addDelSchedule(item)
            }
            override fun bringInfo(item: ScheduleListDto?) {
                val bundle = Bundle()
                bundle.putSerializable("scheduleFix", item) // 날짜 putString으로 dialogFragment에 보내기

                val addFragment = CalScheduleAddFragment()
                addFragment.setAddListener(object: AddDot {
                    override fun onAddDelete(item: ScheduleDto?) {
                        addDelSchedule(item)
                    }
                    override fun bringInfo(item: ScheduleListDto?) {}
                })

                addFragment.arguments = bundle
                addFragment.show(parentFragmentManager, "fixDialog") // 일정추가 dialog 호출
            }
        })

        binding.btnAddSchedule.setOnClickListener { // 일정 추가 dialog 호출
            val bundle = Bundle()
            val text = getCurrentDate() // 월간 화면이 띄워져있으면 월간 화면의 현재 날짜를 putString으로 보내기
            val convertText = convertDateFormat(text)

            val addFragment = CalScheduleAddFragment()
            addFragment.setAddListener(object: AddDot {
                override fun onAddDelete(item: ScheduleDto?) {
                    addDelSchedule(item)
                }
                override fun bringInfo(item: ScheduleListDto?) {}
            })

            bundle.putString("date", convertText) // 날짜 putString으로 dialogFragment에 보내기
            addFragment.arguments = bundle
            addFragment.show(parentFragmentManager, "openAddDialog") // 일정추가 dialog 호출
        }

        binding.dateSelect.setOnClickListener {
            showDatePickDialog()
        }
        binding.preMonth.setOnClickListener {
            val (year, month, day) = calHyg.setPreNextMonthCalendar(binding.txtYearMonth, binding.currentDate, "pre")
            selectedDate = Triple(year, month, day)
            scheduleViewModel.getScheduleList(year, month)
        }
        binding.nextMonth.setOnClickListener {
            val (year, month, day) = calHyg.setPreNextMonthCalendar(binding.txtYearMonth, binding.currentDate, "next")
            selectedDate = Triple(year, month, day)
            scheduleViewModel.getScheduleList(year, month)
        }
        binding.btnToday.setOnClickListener {
            val (year, month, day) = calHyg.setPreNextMonthCalendar(binding.txtYearMonth, binding.currentDate, "today")
            selectedDate = Triple(year, month, day)
            scheduleViewModel.getScheduleList(year, month)
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
                }
            }
        })

        scheduleAdapter.setOnItemClickListener { schedule, _ ->
            val convertSchedule = calHyg.checkFormatSingle(schedule)
            val bundle = Bundle()
            bundle.putSerializable("schedule", convertSchedule)

            infoFragment.arguments = bundle
            infoFragment.show(parentFragmentManager, "scheduleInfo")
        }
    }

    private fun getCurrentDate(): String {  // 월간 프래그먼트의 현재 날짜 getter 함수 (일정 추가 dialog에 날짜 표시에 활용됨)
        return binding.currentDate.text.toString()
    }

    private fun addDelSchedule(item: ScheduleDto?) {
        if (item != null) {
            val (year, month, day) = calHyg.extractInfoS(item)

            selectedDate = Triple(year, month, day)
            scheduleViewModel.getScheduleList(year, month)
        }
    }

    private fun convertDateFormat(input: String): String { // 날짜 형식 변환 함수
        return try {
            val inputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())

            outputFormat.format(inputFormat.parse(input)!!)
        } catch (e: Exception) {
            e.printStackTrace()
            input
        }
    }

    private fun showDatePickDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_date_pick, null)

        calHyg.showDatePickDialog(view, builder, binding.txtYearMonth, binding.currentDate) { info ->
            val (year, month, day) = info
            println("year: $year, month: $month")

            selectedDate = Triple(year, month, day)
            scheduleViewModel.getScheduleList(year, month)
        }

        val dialog = builder.create()
        dialog.show()
    }

//    private fun updateViewModel(adapter: CalListAdapter, date: String) { // 아이템이 추가될 때마다 호출됨(실시간 데이터 변경 감지)
//        calendarViewModel.calMonthList.observe(viewLifecycleOwner) { calList ->
//            adapter.clearItem() // 업데이트 전 리스트 초기화 후 항목을 모두 추가 (중복 삽입 방지)
//
//            calList.forEach { calMonth ->
//                adapter.addItem(calMonth)
//            }
//            adapter.updateList(date)
//        }
//    }

    private fun initScheduleList() { // 일정 리스트 초기화 함수
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.calMonthScheduleView.layoutManager = layoutManager
        binding.calMonthScheduleView.adapter = scheduleAdapter

        observeViewModel(scheduleAdapter)
    }

    private fun observeViewModel(adapter: ScheduleAdapter) {
        scheduleViewModel.apply {
            scheduleList.observe(viewLifecycleOwner) {
                adapter.setItemList(it)
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

interface AddDot {
    fun onAddDelete(item: ScheduleDto? = null)
    fun bringInfo(item: ScheduleListDto?)
}
class CalScheduleAddFragment : BottomSheetDialogFragment() {
    private var _binding: DialogScheduleAddBinding? = null
    private val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by viewModels { ViewModelFactory(ScheduleRepository()) }
    private var listener: AddDot? = null

    fun setAddListener(listener: AddDot) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogScheduleAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = (dialog as? BottomSheetDialog)?.behavior
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initDialog() {
        val receivedDate = arguments?.getString("date") ?: ""

        val title = binding.editTextAddTitle
        val fullTime = binding.isFullCheck
        val info = binding.editTxtSchInfoFix

        fullTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtStartTime.visibility = View.GONE
                binding.txtEndTime.visibility = View.GONE
            } else {
                binding.txtStartTime.visibility = View.VISIBLE
                binding.txtEndTime.visibility = View.VISIBLE
            }
        }

        binding.txtCurrentDateAdd.text = receivedDate
        binding.txtCurrentDateAddEnd.text = receivedDate

        val dialogTitle = binding.txtTitleAddScheduleModify // 다이얼로그 타이틀
        val add = binding.btnAddScheduleDialogModify // 추가버튼(추가, 수정 동시기능)

        val modifiedData = arguments?.getSerializable("scheduleFix") as ScheduleListDto?
        if (modifiedData != null) {          // 수정버튼을 호출한 경우 데이터 수정이 이루어지는 코드 작성
            dialogTitle.text = "일정 수정"      // 제목을 "일정 추가" -> "일정 수정"으로 교체
            add.text = "수정"                  // 버튼텍스트를 추가에서 수정으로 교체
            add.textSize = 18.0f
            fullTime.isChecked = modifiedData.allDay // "종일" 항목을 체크하였는지 여부 설정

            title.setText(modifiedData.title) // 수정시킬 데이터를 불러오는 부분
            info.setText(modifiedData.content)

            // 시작시간, 끝시간 날짜 시간부분 분리하여 각각의 textview에 저장
            splitDateTime(modifiedData.startDate, "start", modifiedData.allDay)
            splitDateTime(modifiedData.endDate, "end", modifiedData.allDay)
        }

        binding.txtStartTime.setOnClickListener {
            showTimePickDialog("start")
        }
        binding.txtEndTime.setOnClickListener {
            showTimePickDialog("end")
        }
        binding.txtCurrentDateAdd.setOnClickListener {
            val currentDate = binding.txtCurrentDateAdd.text.toString()
            showDatePickDialog("start", currentDate)
        }
        binding.txtCurrentDateAddEnd.setOnClickListener {
            val currentDate = binding.txtCurrentDateAddEnd.text.toString()
            showDatePickDialog("end", currentDate)
        }

        add.setOnClickListener {
            val data = bringCurrentData(title, info, fullTime)

            if (title.text.toString().isEmpty()) { // 제목 미 입력시 입력 하도록 설계
                Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                if (add.text.equals("+")) {
                    scheduleViewModel.addSchedule(data)
                    Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        listener?.onAddDelete(data)
                    }, 100) // 1000 밀리초(1초) 후에 실행
                }
                else if (add.text.equals("수정")) {
                    scheduleViewModel.updateSchedule(modifiedData!!.scheduleId, data)
                    Toast.makeText(requireContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        listener?.onAddDelete(data)
                    }, 100)
                }
                else { Toast.makeText(requireContext(), "일정 추가/수정 오류", Toast.LENGTH_SHORT).show() }

                dismiss()
            }
        }
    }

    // 작성한 데이터를 일정에 등록 하기 위한 데이터 리턴 함수
    private fun bringCurrentData(title: EditText, info: EditText, fullTime: CheckBox): ScheduleDto {
        val currentDateStart = binding.txtCurrentDateAdd.text.toString().replace(" - ", "-")
        val currentDateEnd = binding.txtCurrentDateAddEnd.text.toString().replace(" - ", "-")
        val data: ScheduleDto

        val times = timeTakeLast(binding.txtStartTime.text.toString(), binding.txtEndTime.text.toString())

        if (fullTime.isChecked) { // 종일이 체크되어 있으면 시간대는 "종일"로 기록, 아니면 시간대를 저장
            data = ScheduleDto("${currentDateStart}T00:00", "${currentDateEnd}T00:00", fullTime.isChecked,
                title.text.toString(), info.text.toString())
        }
        else data = ScheduleDto("${currentDateStart}T${timeCalculate(times.first)}",
            "${currentDateEnd}T${timeCalculate(times.second)}", fullTime.isChecked, title.text.toString(), info.text.toString())

        return data
    }

    private fun timeCalculate(input: Int): String { // int로 입력 받은 시간값 24시간 형식으로 반환
        val timeWithLeadingZeros = String.format("%04d", input)

        val inputFormat = SimpleDateFormat("HHmm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val date = inputFormat.parse(timeWithLeadingZeros)
        return outputFormat.format(date!!)
    }

    private fun splitDateTime(dateTime: String, status: String, isChecked: Boolean) { // (수정모드에서만 호출되는 함수)
        val date = dateTime.take(14)
        val time = dateTime.takeLast(8)

        if (!isChecked) {
            when (status) {
                "start" -> {
                    binding.txtCurrentDateAdd.text = date
                    binding.txtStartTime.text = time
                }
                "end" -> {
                    binding.txtCurrentDateAddEnd.text = date
                    binding.txtEndTime.text = time
                }
            }
        } else {
            when (status) {
                "start" -> binding.txtCurrentDateAdd.text = date
                "end" -> binding.txtCurrentDateAddEnd.text = date
            }
        }
    }

    private fun showTimePickDialog(status: String) { // 시간 선택 다이얼로그 호출 함수
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater

        val bindingD = DialogTimePickBinding.inflate(inflater)

        val noon = bindingD.pickNoon // 오전/오후 선택
        val hour = bindingD.pickHour // 시간 선택
        val minute = bindingD.pickMinute // 분 선택

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
                    autoSetEndTime(timeValue, binding.txtEndTime.text.toString())
                    binding.txtStartTime.text = timeValue
                }
                else if (status == "end") {
                    if (checkIfReversedTime(binding.txtStartTime.text.toString(), timeValue))
                        binding.txtEndTime.text = timeValue
                }
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.setView(bindingD.root)

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
            if (checkIfReversed(formattedDate, binding.txtCurrentDateAddEnd.text.toString(), status))
                binding.txtCurrentDateAdd.text = formattedDate
        }
        else if (status == "end") {
            if (checkIfReversed(binding.txtCurrentDateAdd.text.toString(), formattedDate, status))
                binding.txtCurrentDateAddEnd.text = formattedDate
        }
    }

    private fun timeValue(noon: Int, hour: Int, minute: Int): String { // dialog 선택후 textview에 출력될 테스트 반환 함수
        val isNoon = if (noon == 0) "오전" else "오후"
        return "$isNoon ${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    private fun checkIfReversed(start: String, end: String, status: String): Boolean { // 시작날짜와 끝날짜가 서로가 서로보다 앞서거나 뒤서면 감지하는 함수
        val first = start.replace(" - ", "").toInt()
        val second = end.replace(" - ", "").toInt()
        val (startT, endT) = timeTakeLast(binding.txtStartTime.text.toString(), binding.txtEndTime.text.toString())

        return if (first > second || (first == second && startT > endT)) {
            if (status == "start") {
                binding.txtCurrentDateAdd.text = start // 끝날짜를 시작날짜와 같게 설정. (날짜선택을 제한적으로 하지 않게 하려는 배려)
                binding.txtCurrentDateAddEnd.text = start
            }
            else if (status == "end") {
                Toast.makeText(requireContext(), "날짜 설정 오류입니다.", Toast.LENGTH_SHORT).show()
            }
            false
        }
        else true
    }

    private fun getStartAndEndDates(): Pair<String, String> {
        return Pair(binding.txtCurrentDateAdd.text.toString(), binding.txtCurrentDateAddEnd.text.toString())
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

            binding.txtEndTime.text = resultEndTime

            if (resultEndTime.contains("오전 12:")) { // 자동 설정된 끝 시간이 오전 12시 이상 (다음 날로 넘어 가는 경우) 긑날짜 1일 증가.
                val regex = Regex("(\\d{4} - \\d{2} - \\d{2})")
                val text = binding.txtCurrentDateAddEnd.text.toString()

                val resultDateEnd = text.replace(regex) {
                    val date = it.groupValues[1]

                    // LocalDate로 파싱
                    val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy - MM - dd"))

                    // 날짜의 일 부분을 1 증가시키고 문자열로 변환
                    val modifiedDate = parsedDate.plusDays(1)
                    modifiedDate.format(DateTimeFormatter.ofPattern("yyyy - MM - dd"))
                }
                binding.txtCurrentDateAddEnd.text = resultDateEnd
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

        return Pair(first, second)
    }
}

// 일정 상세 화면 다이얼로그
class CalScheduleInfoFragment : BottomSheetDialogFragment() {
    private var _binding: DialogScheduleInfoBinding? = null
    private val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by viewModels { ViewModelFactory(ScheduleRepository()) }
    private var listener: AddDot? = null

    fun setAddListener(listener: AddDot) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogScheduleInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            val item = bundle.getSerializable("schedule") as ScheduleListDto
            val scheduleId = item.scheduleId

            showInfo(item)
            binding.btnSchDeleteInfo.setOnClickListener {
                deleteSchedule(item, scheduleId)
            }
            binding.btnSchModifyInfo.setOnClickListener {
                modifySchedule(item)
                dismiss()
            }
        } else {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showInfo(item: ScheduleListDto?) { // 일정 정보 출력 함수
        if (item == null) return
        else {
            binding.apply {
                txtSchPeriodInfo.text = item.startDate
                txtSchPeriodInfoEnd.text = item.endDate
                txtSchTitleInfo.text = item.title
                txtSchDesInfo.text = item.content
            }
        }
    }

    private fun deleteSchedule(item: ScheduleListDto?, scheduleId: Int) { // 일정 삭제 함수
        if (item == null) return
        else {
            scheduleViewModel.deleteSchedule(scheduleId)
            Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()

            val returnItem = CalendarHyg().revertFormat(
                ScheduleDto(item.startDate, item.endDate, item.allDay, item.title, item.content)
            )
            Handler(Looper.getMainLooper()).postDelayed({
                listener?.onAddDelete(returnItem)
            }, 100) // 0.1초 후 실행
            dismiss()
        }
    }

    private fun modifySchedule(item: ScheduleListDto?) { // 일정 수정 함수
        if (item == null) return
        else { listener?.bringInfo(item) }
    }
}

