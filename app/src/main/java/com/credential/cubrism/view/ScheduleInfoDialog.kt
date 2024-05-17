package com.credential.cubrism.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.credential.cubrism.databinding.DialogScheduleInfoBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScheduleInfoDialog(private val scheduleListDto: ScheduleListDto, private val onUpdate: () -> Unit, private val onDelete: (scheduleId: Int) -> Unit) : BottomSheetDialogFragment() {
    private var _binding: DialogScheduleInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogScheduleInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        binding.txtTitle.text = scheduleListDto.title
        binding.txtContent.text = scheduleListDto.content
        binding.txtStartDateTime.text = if (scheduleListDto.allDay) {
            convertDateTimeFormat(scheduleListDto.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd")
        } else {
            convertDateTimeFormat(scheduleListDto.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd hh:mm a")
        }
        binding.txtEndDateTime.text = if (scheduleListDto.allDay) {
            convertDateTimeFormat(scheduleListDto.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd")
        } else {
            convertDateTimeFormat(scheduleListDto.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd hh:mm a")
        }

        binding.btnSchModifyInfo.setOnClickListener {
            onUpdate()
            dismiss()
        }

        binding.btnSchDeleteInfo.setOnClickListener {
            onDelete(scheduleListDto.scheduleId)
            dismiss()
        }
    }
}