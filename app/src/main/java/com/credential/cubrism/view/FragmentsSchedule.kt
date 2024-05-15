package com.credential.cubrism.view

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.DialogMenuBinding
import com.credential.cubrism.databinding.DialogScheduleAddBinding
import com.credential.cubrism.databinding.DialogScheduleDatepickBinding
import com.credential.cubrism.databinding.DialogScheduleInfoBinding
import com.credential.cubrism.databinding.DialogTimePickBinding
import com.credential.cubrism.databinding.FragmentScheduleBinding
import com.credential.cubrism.model.dto.MenuDto
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.model.repository.ScheduleRepository
import com.credential.cubrism.view.adapter.CalendarAdapter
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

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val myApplication = MyApplication.getInstance()

    private val scheduleViewModel: ScheduleViewModel by viewModels { ViewModelFactory(ScheduleRepository()) }

    private val calendarAdapter = CalendarAdapter()
    private val scheduleAdapter = ScheduleAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        binding.imgAddSchedule.setOnClickListener {
            ScheduleAddUpdateDialog(ScheduleType.ADD, requireContext(), null,
                onClick = {
                    scheduleViewModel.addSchedule(it)
                }
            ).show(parentFragmentManager, "addSchedule")
        }
    }

    private fun observeViewModel() {
        scheduleViewModel.apply {
            addSchedule.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                scheduleViewModel.getScheduleList(null, null)
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
//    private fun initCalendarSchedule() {
//        initScheduleList()
//
//        val infoFragment = CalScheduleInfoFragment()
//        infoFragment.setAddListener(object: AddDot {
//            override fun onAddDelete(item: ScheduleDto?) {
//                addDelSchedule(item)
//            }
//            override fun bringInfo(item: ScheduleListDto?) {
//                val bundle = Bundle()
//                bundle.putSerializable("scheduleFix", item) // 날짜 putString으로 dialogFragment에 보내기
//
//                val addFragment = CalScheduleAddFragment()
//                addFragment.setAddListener(object: AddDot {
//                    override fun onAddDelete(item: ScheduleDto?) {
//                        addDelSchedule(item)
//                    }
//                    override fun bringInfo(item: ScheduleListDto?) {}
//                })
//
//                addFragment.arguments = bundle
//                addFragment.show(parentFragmentManager, "fixDialog") // 일정추가 dialog 호출
//            }
//        })
//
//        binding.btnAddSchedule.setOnClickListener { // 일정 추가 dialog 호출
//            val bundle = Bundle()
//            val text = getCurrentDate() // 월간 화면이 띄워져있으면 월간 화면의 현재 날짜를 putString으로 보내기
//            val convertText = convertDateFormat(text)
//
//            val addFragment = CalScheduleAddFragment()
//            addFragment.setAddListener(object: AddDot {
//                override fun onAddDelete(item: ScheduleDto?) {
//                    addDelSchedule(item)
//                }
//                override fun bringInfo(item: ScheduleListDto?) {}
//            })
//
//            val isLoggedIn = myApplication.getUserData().getLoginStatus()
//            if (isLoggedIn) {
//                bundle.putString("date", convertText) // 날짜 putString으로 dialogFragment에 보내기
//                addFragment.arguments = bundle
//                addFragment.show(parentFragmentManager, "openAddDialog") // 일정추가 dialog 호출
//            } else {
//                Toast.makeText(requireContext(), "로그인하여 일정 기능을 이용해보세요!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.dateSelect.setOnClickListener { showDatePickDialog() }
//        binding.preMonth.setOnClickListener { changeCalendarStatus("pre") }
//        binding.nextMonth.setOnClickListener { changeCalendarStatus("next") }
//        binding.btnToday.setOnClickListener { changeCalendarStatus("today") }
//
//        calendarAdapter.setItemClickListener(object: DateMonthClickListener {
//            override fun onItemClicked(item: DateSelect) {
//                if (item.date.isDigitsOnly()) {
//                    val day = item.date
//                    val regex = Regex("\\d+일")
//                    val text = binding.currentDate.text.toString().replace(regex, "${day}일")
//
//                    // 달력의 날짜 누르면 textview 날짜 갱신
//                    binding.currentDate.text = text
//                    calendarAdapter.highlightCurrentDate(item, true)
//                    refreshScheduleList(day.toInt())
//                }
//            }
//        })
//
//        scheduleAdapter.setOnItemClickListener { schedule, _ ->
//            val convertSchedule = calHyg.checkFormatSingle(schedule)
//            val bundle = Bundle()
//            bundle.putSerializable("schedule", convertSchedule)
//
//            infoFragment.arguments = bundle
//            infoFragment.show(parentFragmentManager, "scheduleInfo")
//        }
//    }
//
//    private fun getCurrentDate(): String {  // 월간 프래그먼트의 현재 날짜 getter 함수 (일정 추가 dialog에 날짜 표시에 활용됨)
//        return binding.currentDate.text.toString()
//    }
//
//    private fun addDelSchedule(item: ScheduleDto?) {
//        if (item != null) {
//            val (year, month, day) = calHyg.extractInfoS(item)
//
//            selectedDate = Triple(year, month, day)
//            scheduleViewModel.getScheduleList(year, month)
//            refreshScheduleList(day)
//        }
//    }
//
//    private fun convertDateFormat(input: String): String { // 날짜 형식 변환 함수
//        return try {
//            val inputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
//            val outputFormat = SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())
//
//            outputFormat.format(inputFormat.parse(input)!!)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            input
//        }
//    }
//
//    private fun showDatePickDialog() {
//        val builder = AlertDialog.Builder(requireActivity())
//        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.dialog_date_pick, null)
//
//        calHyg.showDatePickDialog(view, builder, binding.txtYearMonth, binding.currentDate) { info ->
//            val (year, month, day) = info
//
//            val isLoggedIn = myApplication.getUserData().getLoginStatus()
//            if (isLoggedIn) {
//                selectedDate = Triple(year, month, day)
//                scheduleViewModel.getScheduleList(year, month)
//                refreshScheduleList(day)
//            } else {
//                calHyg.showMonthCalendarNew(year, month, day, listOf(), calendarAdapter)
//            }
//
//        }
//
//        val dialog = builder.create()
//        dialog.show()
//    }
//
//    private fun initScheduleList() { // 일정 리스트 초기화 함수
//        binding.calendarRealView.layoutManager = GridLayoutManager(requireContext(), 7) // 월간 달력 recyclerView 초기화
//        binding.calendarRealView.adapter = calendarAdapter
//
//        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.calMonthScheduleView.layoutManager = layoutManager
//        binding.calMonthScheduleView.adapter = scheduleAdapter
//    }
//
//    private fun changeCalendarStatus(status: String? = null) {
//        val (year, month, day) = if (status == null) {
//            calHyg.initToday(binding.txtYearMonth, binding.currentDate)
//        } else {
//            calHyg.setPreNextMonthCalendar(binding.txtYearMonth, binding.currentDate, status)
//        }
//
//        val isLoggedIn = myApplication.getUserData().getLoginStatus()
//        if (isLoggedIn) {
//            selectedDate = Triple(year, month, day)
//            scheduleViewModel.getScheduleList(year, month)
//            refreshScheduleList(day)
//        } else {
//            calHyg.showMonthCalendarNew(year, month, day, listOf(), calendarAdapter)
//            scheduleAdapter.setItemList(listOf())
//        }
//    }
//
//    private fun refreshScheduleList(day: Int) {
//        Handler(Looper.getMainLooper()).postDelayed({
//            val format = calHyg.getSelectedYearMonth(binding.txtYearMonth.text.toString())
//            val dateInt = "${format.first}${String.format("%02d", format.second)}${String.format("%02d", day)}".toInt()
//            scheduleAdapter.setItemListDay(currentMonthList, dateInt)
//        }, 100)
//    }
//
//    private fun observeViewModel() {
//        scheduleViewModel.apply {
//            scheduleList.observe(viewLifecycleOwner) {
//                currentMonthList = it
//                selectedDate?.let { (year, month, day) ->
//                    calHyg.showMonthCalendarNew(year, month, day, it, calendarAdapter)
//                }
//                selectedDate = null
//            }
//
//            errorMessage.observe(viewLifecycleOwner) { event ->
//                event.getContentIfNotHandled()?.let { message ->
//                    if (message != "JWT 토큰이 잘못되었습니다.")
//                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
}