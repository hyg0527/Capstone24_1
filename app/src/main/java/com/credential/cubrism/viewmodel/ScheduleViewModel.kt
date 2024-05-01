package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.ScheduleDto
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.model.repository.ScheduleRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class ScheduleViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {
    private val _addSchedule = MutableLiveData<MessageDto>()
    val addSchedule: LiveData<MessageDto> = _addSchedule

    private val _deleteSchedule = MutableLiveData<MessageDto>()
    val deleteSchedule: LiveData<MessageDto> = _deleteSchedule

    private val _updateSchedule = MutableLiveData<MessageDto>()
    val updateSchedule: LiveData<MessageDto> = _updateSchedule

    private val _scheduleList = MutableLiveData<List<ScheduleListDto>>()
    val scheduleList: LiveData<List<ScheduleListDto>> = _scheduleList

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun addSchedule(scheduleDto: ScheduleDto) {
        scheduleRepository.addSchedule(scheduleDto) { result ->
            handleResult(result, _addSchedule, _errorMessage)
        }
    }

    fun deleteSchedule(scheduleId: Int) {
        scheduleRepository.deleteSchedule(scheduleId) { result ->
            handleResult(result, _deleteSchedule, _errorMessage)
        }
    }

    fun updateSchedule(scheduleId: Int, scheduleDto: ScheduleDto) {
        scheduleRepository.updateSchedule(scheduleId, scheduleDto) { result ->
            handleResult(result, _updateSchedule, _errorMessage)
        }
    }

    fun getScheduleList(year: Int?, month: Int?) {
        scheduleRepository.getScheduleList(year, month) { result ->
            when (result) {
                is ResultUtil.Success -> { _scheduleList.postValue(result.data) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    private fun handleResult(result: ResultUtil<MessageDto>, successLiveData: MutableLiveData<MessageDto>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}