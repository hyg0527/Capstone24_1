package com.credential.cubrism.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.credential.cubrism.databinding.DialogScheduleAddBinding
import com.credential.cubrism.databinding.DialogScheduleDatepickBinding
import com.credential.cubrism.databinding.DialogTimePickBinding
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.converLocalDateTimeToString
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertStringToLocalDateTime
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ScheduleType {
    ADD, UPDATE
}

class ScheduleAddUpdateDialog(private val scheduleType: ScheduleType, private val context: Context, private val date: LocalDate?, private val scheduleListDto: ScheduleListDto?, private val onClick: (scheduleDto: ScheduleDto) -> Unit) : BottomSheetDialogFragment() {
    private var _binding: DialogScheduleAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogScheduleAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDialog() {
        val bottomSheetBehavior = (dialog as? BottomSheetDialog)?.behavior
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setupView() {
        when (scheduleType) {
            // 일정 추가
            ScheduleType.ADD -> {
                val now = LocalDateTime.now()
                val startDateTime = (date ?: LocalDate.now()).atTime(now.hour, now.minute).plusHours(1)
                val endDateTime = startDateTime.plusHours(1)

                binding.txtStartDate.text = startDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                binding.txtEndDate.text = endDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                binding.txtStartTime.text =  "${startDateTime.format(DateTimeFormatter.ofPattern("a hh", Locale.KOREA))}:00"
                binding.txtEndTime.text = "${endDateTime.format(DateTimeFormatter.ofPattern("a hh", Locale.KOREA))}:00"

                binding.txtTitleAddScheduleModify.text = "일정 추가"
                binding.btnAddScheduleDialogModify.apply {
                    text = "+"
                    textSize = 40.0f
                }
            }
            // 일정 수정
            ScheduleType.UPDATE -> {
                scheduleListDto?.let {
                    binding.editTextAddTitle.setText(it.title)
                    binding.editTxtSchInfoFix.setText(it.content)
                    binding.txtStartDate.text = convertDateTimeFormat(it.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd")
                    binding.txtStartTime.text = convertDateTimeFormat(it.startDate, "yyyy-MM-dd'T'HH:mm", "a hh:mm")
                    binding.txtEndDate.text = convertDateTimeFormat(it.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd")
                    binding.txtEndTime.text = convertDateTimeFormat(it.endDate, "yyyy-MM-dd'T'HH:mm", "a hh:mm")
                    binding.isFullCheck.isChecked = it.allDay
                    binding.txtStartTime.visibility = if (it.allDay) View.GONE else View.VISIBLE
                    binding.txtEndTime.visibility = if (it.allDay) View.GONE else View.VISIBLE
                }

                binding.txtTitleAddScheduleModify.text = "일정 수정"
                binding.btnAddScheduleDialogModify.apply{
                    text = "수정"
                    textSize = 18.0f
                }
            }
        }

        // 종일을 선택하면 시간 숨기기
        binding.isFullCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.txtStartTime.visibility = if (isChecked) View.GONE else View.VISIBLE
            binding.txtEndTime.visibility = if (isChecked) View.GONE else View.VISIBLE

            if (!isChecked && binding.txtStartTime.text == "오전 12:00" && binding.txtEndTime.text == "오전 12:00") {
                binding.txtStartTime.text = "오전 12:00"
                binding.txtEndTime.text = "오전 01:00"
            }
        }

        binding.txtStartDate.setOnClickListener {
            showDatePickDialog("start", binding.txtStartDate.text.toString())
        }

        binding.txtEndDate.setOnClickListener {
            showDatePickDialog("end", binding.txtEndDate.text.toString())
        }

        binding.txtStartTime.setOnClickListener {
            showTimePickDialog("start", binding.txtStartTime.text.toString())
        }

        binding.txtEndTime.setOnClickListener {
            showTimePickDialog("end", binding.txtEndTime.text.toString())
        }

        binding.btnAddScheduleDialogModify.setOnClickListener {
            if (binding.editTextAddTitle.text?.isEmpty() == true) {
                Toast.makeText(context, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDateTime = if (binding.isFullCheck.isChecked) {
                "${binding.txtStartDate.text.toString().replace(".", "-")}T00:00:00"
            } else {
                "${convertDateTimeFormat(binding.txtStartDate.text.toString() + " " + binding.txtStartTime.text.toString(), "yyyy.MM.dd a hh:mm", "yyyy-MM-dd'T'HH:mm")}:00"
            }

            val endDateTime = if (binding.isFullCheck.isChecked) {
                "${binding.txtEndDate.text.toString().replace(".", "-")}T00:00:00"
            } else {
                "${convertDateTimeFormat(binding.txtEndDate.text.toString() + " " + binding.txtEndTime.text.toString(), "yyyy.MM.dd a hh:mm", "yyyy-MM-dd'T'HH:mm")}:00"
            }

            onClick(ScheduleDto(
                title = binding.editTextAddTitle.text.toString(),
                content = binding.editTxtSchInfoFix.text.toString(),
                startDate = startDateTime,
                endDate = endDateTime,
                isAllDay = binding.isFullCheck.isChecked
            ))
            dismiss()
        }
    }

    private fun showTimePickDialog(status: String, time: String) { // 시간 선택 다이얼로그 호출 함수
        val dialogTimePickBinding = DialogTimePickBinding.inflate(requireActivity().layoutInflater)

        val noon = time.substringBefore(" ")
        val hour = time.substringAfter(" ").substringBefore(":").toInt()
        val minute = time.substringAfter(":").toInt()

        dialogTimePickBinding.pickNoon.apply {
            minValue = 0
            maxValue = 1
            value = if (noon == "오전") 0 else 1
            displayedValues = arrayOf("오전", "오후")
            wrapSelectorWheel = false // 오전/오후 선택 부분만 순환 하지 않도록 설정
        }

        dialogTimePickBinding.pickHour.apply {
            minValue = 1
            maxValue = 12
            value = hour
            setFormatter { String.format(Locale.getDefault(), "%02d", it) }
        }

        dialogTimePickBinding.pickMinute.apply {
            minValue = 0
            maxValue = 59
            value = minute
            setFormatter { String.format(Locale.getDefault(), "%02d", it) }
        }

        AlertDialog.Builder(context).apply {
            setView(dialogTimePickBinding.root)
            setTitle("시간 선택")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                val timeValue = timeValue(
                    noon = dialogTimePickBinding.pickNoon.displayedValues[dialogTimePickBinding.pickNoon.value],
                    hour = dialogTimePickBinding.pickHour.value,
                    minute = dialogTimePickBinding.pickMinute.value
                )

                when (status) {
                    "start" -> {
                        val startDateTime = combineDateTime(binding.txtStartDate.text.toString(), timeValue)
                        var endDateTime = getEndDateTime()
                        if (!isStartBeforeEnd(startDateTime, endDateTime)) {
                            endDateTime = startDateTime.plusHours(1)
                            val endDate = converLocalDateTimeToString(endDateTime, "yyyy.MM.dd")
                            val endTime = converLocalDateTimeToString(endDateTime, "a hh:mm")
                            binding.txtEndDate.text = endDate
                            binding.txtEndTime.text = endTime
                        }
                        binding.txtStartTime.text = timeValue
                    }
                    "end" -> {
                        val endDateTime = combineDateTime(binding.txtEndDate.text.toString(), timeValue)
                        val startDateTime = getStartDateTime()
                        if (isStartBeforeEnd(startDateTime, endDateTime)) {
                            binding.txtEndTime.text = timeValue
                        } else {
                            Toast.makeText(context, "종료 시간을 시작 시간 이후로 설정해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            show()
        }
    }

    // dialog 선택후 textview에 출력될 테스트 반환 함수
    private fun timeValue(noon: String, hour: Int, minute: Int): String = "$noon ${String.format(Locale.getDefault(),"%02d", hour)}:${String.format(Locale.getDefault(), "%02d", minute)}"

    // 날짜와 시간을 합치는 함수
    private fun combineDateTime(date: String, time: String): LocalDateTime = convertStringToLocalDateTime("$date $time", "yyyy.MM.dd a hh:mm")

    // 시작 날짜와 시간을 반환하는 함수
    private fun getStartDateTime(): LocalDateTime = convertStringToLocalDateTime("${binding.txtStartDate.text} ${binding.txtStartTime.text}", "yyyy.MM.dd a hh:mm")

    // 종료 날짜와 시간을 반환하는 함수
    private fun getEndDateTime(): LocalDateTime = convertStringToLocalDateTime("${binding.txtEndDate.text} ${binding.txtEndTime.text}", "yyyy.MM.dd a hh:mm")

    // 시작 시간이 종료 시간보다 빠른지 확인하는 함수
    private fun isStartBeforeEnd(start: LocalDateTime, end: LocalDateTime): Boolean = start < end



    private fun showDatePickDialog(status: String, dateString: String) { // 날짜 선택 다이얼로그 창 출력 함수
        val dialogScheduleDatepickBinding = DialogScheduleDatepickBinding.inflate(requireActivity().layoutInflater)

        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: Date()

        val calInstance = Calendar.getInstance()
        calInstance.time = date

        dialogScheduleDatepickBinding.calendarViewDialog.date = calInstance.timeInMillis

        var selectedDate = Date()
        dialogScheduleDatepickBinding.calendarViewDialog.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calInstance.set(year, month, dayOfMonth)
            selectedDate = calInstance.time
        }

        AlertDialog.Builder(context).apply {
            setView(dialogScheduleDatepickBinding.root)
            setTitle("날짜 선택")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                when (status) {
                    "start" -> {
                        val startDateTime = combineDateTime(dateFormat.format(selectedDate), binding.txtStartTime.text.toString())
                        var endDateTime = getEndDateTime()
                        if (!isStartBeforeEnd(startDateTime, endDateTime)) {
                            endDateTime = startDateTime.plusDays(1)
                            val endDate = converLocalDateTimeToString(endDateTime, "yyyy.MM.dd")
                            binding.txtEndDate.text = endDate
                        }
                        binding.txtStartDate.text = dateFormat.format(calInstance.time)
                    }
                    "end" -> {
                        val endDateTime = combineDateTime(dateFormat.format(selectedDate), binding.txtEndTime.text.toString())
                        val startDateTime = getStartDateTime()
                        if (isStartBeforeEnd(startDateTime, endDateTime)) {
                            binding.txtEndDate.text = dateFormat.format(calInstance.time)
                        } else {
                            Toast.makeText(context, "종료 날짜를 시작 날짜 이후로 설정해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            show()
        }
    }
}