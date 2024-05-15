package com.credential.cubrism.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.credential.cubrism.MyApplication
import com.credential.cubrism.databinding.DialogDatePickBinding
import com.credential.cubrism.databinding.FragmentScheduleBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.model.repository.ScheduleRepository
import com.credential.cubrism.view.adapter.CalendarAdapter
import com.credential.cubrism.view.adapter.ScheduleAdapter
import com.credential.cubrism.view.utils.CalendarUtil
import com.credential.cubrism.viewmodel.ScheduleViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val calendarUtil = CalendarUtil()

    private val scheduleViewModel: ScheduleViewModel by activityViewModels { ViewModelFactory(ScheduleRepository()) }

    private val calendarAdapter = CalendarAdapter()
    private val scheduleAdapter = ScheduleAdapter()

    private var schedules = mutableListOf<ScheduleListDto>()
    private var isLoggedIn = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        setupViewWithLoginStatus()
    }

    private fun setupView() {
        val (year, month, day) = calendarUtil.initToday()
        updateCalendar(year, month, day)

        binding.txtYearMonth.setOnClickListener {
            showDatePickDialog()
        }

        binding.txtPreMonth.setOnClickListener {
            val (prevYear, preMonth) = calendarUtil.getPrevNextMonth(binding.txtYearMonth.text.toString(), "prev")
            updateCalendar(prevYear, preMonth)
        }

        binding.txtNextMonth.setOnClickListener {
            val (nextYear, nextMonth) = calendarUtil.getPrevNextMonth(binding.txtYearMonth.text.toString(), "next")
            updateCalendar(nextYear, nextMonth)
        }

        binding.txtToday.setOnClickListener {
            updateCalendar(year, month, day)
        }

        binding.imgAddSchedule.setOnClickListener {
            if (isLoggedIn) {
                ScheduleAddUpdateDialog(
                    ScheduleType.ADD,
                    requireContext(),
                    calendarUtil.getSelectedYearMonthDay(binding.txtDate.text.toString()),
                    null,
                    onClick = {
                        scheduleViewModel.addSchedule(it)
                    }
                ).show(parentFragmentManager, "addSchedule")
            } else {
                Toast.makeText(requireContext(), "로그인하여 일정 기능을 이용해보세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewWithLoginStatus() {
        isLoggedIn = MyApplication.getInstance().getUserData().getLoginStatus()
        if (isLoggedIn)
            binding.txtNoSchedule.text = "등록된 일정이 없습니다."
        else
            binding.txtNoSchedule.text = "로그인하여 일정을 추가해보세요!"
    }

    private fun setupRecyclerView() {
        binding.recyclerCalendar.apply {
            adapter = calendarAdapter
            itemAnimator = null
        }

        calendarAdapter.apply {
            setOnItemClickListener { item, position ->
                if (item.date != null) {
                    setHighlightItem(item, position)
                    binding.txtDate.text = "${item.year}년 ${item.month}월 ${item.date}일"
                    scheduleAdapter.setItemListDay(schedules, calendarUtil.getSelectedYearMonthDay(binding.txtDate.text.toString()))
                }
            }
        }

        binding.recyclerSchedule.apply {
            adapter = scheduleAdapter
            itemAnimator = null
        }

        scheduleAdapter.setOnItemClickListener { item, _ ->
            ScheduleInfoDialog(item,
                onUpdate = {
                    ScheduleAddUpdateDialog(
                        ScheduleType.UPDATE,
                        requireContext(),
                        null,
                        item,
                        onClick = {
                            scheduleViewModel.updateSchedule(item.scheduleId, it)
                        }
                    ).show(parentFragmentManager, "addSchedule")
                },
                onDelete = {
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage("일정을 삭제하시겠습니까?")
                        setNegativeButton("취소", null)
                        setPositiveButton("확인") { _, _ ->
                            scheduleViewModel.deleteSchedule(it)
                        }
                        show()
                    }
                }
            ).show(parentFragmentManager, "scheduleInfo")
        }
    }

    private fun observeViewModel() {
        scheduleViewModel.apply {
            scheduleList.observe(viewLifecycleOwner) { list ->
                binding.progressIndicator.hide()
                schedules = list.toMutableList()
                calendarAdapter.clearScheduledItem()
                calendarAdapter.setScheduledItem(schedules)
                scheduleAdapter.setItemListDay(schedules, calendarUtil.getSelectedYearMonthDay(binding.txtDate.text.toString()))
            }

            addSchedule.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                scheduleViewModel.getScheduleList(null, null)
            }

            updateSchedule.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                scheduleViewModel.getScheduleList(null, null)
            }

            deleteSchedule.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                scheduleViewModel.getScheduleList(null, null)
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                binding.progressIndicator.hide()
                event.getContentIfNotHandled()?.let { message ->
                    if (message != "JWT 토큰이 잘못되었습니다.") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    } else {
                        schedules.clear()
                        calendarAdapter.clearScheduledItem()
                        scheduleAdapter.setItemList(emptyList())
                    }
                }
            }
        }
    }

    // 달력 및 일정 업데이트
    private fun updateCalendar(year: Int, month: Int, day: Int = 1) {
        binding.txtYearMonth.text = "${year}년 ${month}월"
        binding.txtDate.text = "${year}년 ${month}월 ${day}일"

        calendarAdapter.setItemList(calendarUtil.showCalendar(year, month, day)) // 해당 달의 달력 표시
        calendarAdapter.setScheduledItem(schedules) // 일정이 있는 날짜에 점 표시
        scheduleAdapter.setItemListDay(schedules, calendarUtil.getSelectedYearMonthDay("${year}년 ${month}월 ${day}일")) // 해당 날짜의 일정 표시
    }

    private fun showDatePickDialog() {
        val dialogDatePickBinding = DialogDatePickBinding.inflate(layoutInflater)

        val (year, month) = calendarUtil.getSelectedYearMonth(binding.txtYearMonth.text.toString())

        dialogDatePickBinding.yearPick.apply {
            minValue = 1950
            maxValue = 2050
            value = year
            wrapSelectorWheel = false
        }

        dialogDatePickBinding.monthPick.apply {
            minValue = 1
            maxValue = 12
            value = month
            displayedValues = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        }

        AlertDialog.Builder(requireContext()).apply {
            setView(dialogDatePickBinding.root)
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                val selectedYear = dialogDatePickBinding.yearPick.value
                val selectedMonth = dialogDatePickBinding.monthPick.value
                updateCalendar(selectedYear, selectedMonth)
            }
            show()
        }
    }
}