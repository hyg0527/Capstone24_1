package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.MajorFieldDto
import com.credential.cubrism.model.dto.MiddleFieldDto
import com.credential.cubrism.model.dto.QualificationDetailsDto
import com.credential.cubrism.model.dto.QualificationListDto
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class QualificationViewModel(private val repository: QualificationRepository) : ViewModel() {
    private val _qualificationList = MutableLiveData<List<QualificationListDto>>()
    val qualificationList: LiveData<List<QualificationListDto>> = _qualificationList

    private val _majorFieldList = MutableLiveData<List<MajorFieldDto>>()
    val majorFieldList: LiveData<List<MajorFieldDto>> = _majorFieldList

    private val _middleFieldList = MutableLiveData<List<MiddleFieldDto>>()
    val middleFieldList: LiveData<List<MiddleFieldDto>> = _middleFieldList

    private val _qualificationDetails = MutableLiveData<QualificationDetailsDto>()
    val qualificationDetails: LiveData<QualificationDetailsDto> = _qualificationDetails

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getQualificationList() {
        repository.qualificationList { result ->
            handleResult(result, _qualificationList, _errorMessage)
        }
    }

    fun getMajorFieldList() {
        repository.majorFieldList { result ->
            handleResult(result, _majorFieldList, _errorMessage)
        }
    }

    fun getMiddleFieldList(field: String) {
        repository.middleFieldList(field) { result ->
            handleResult(result, _middleFieldList, _errorMessage)
        }
    }

    fun getQualificationDetails(code: String) {
        repository.qualificationDetails(code) { result ->
            handleResult(result, _qualificationDetails, _errorMessage)
        }
    }

    private fun <T> handleResult(result: ResultUtil<T>, successLiveData: MutableLiveData<T>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}