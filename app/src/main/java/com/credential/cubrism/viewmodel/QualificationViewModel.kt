package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.MajorFieldDto
import com.credential.cubrism.model.dto.MiddleFieldDto
import com.credential.cubrism.model.dto.QualificationListDto
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.utils.ResultUtil

class QualificationViewModel(private val repository: QualificationRepository) : ViewModel() {
    private val _qualificationList = MutableLiveData<ResultUtil<List<QualificationListDto>>>()
    val qualificationList: LiveData<ResultUtil<List<QualificationListDto>>> = _qualificationList

    private val _majorFieldList = MutableLiveData<ResultUtil<List<MajorFieldDto>>>()
    val majorFieldList: LiveData<ResultUtil<List<MajorFieldDto>>> = _majorFieldList

    private val _middleFieldList = MutableLiveData<ResultUtil<List<MiddleFieldDto>>>()
    val middleFieldList: LiveData<ResultUtil<List<MiddleFieldDto>>> = _middleFieldList

    fun getQualificationList() {
        repository.qualificationList { result ->
            _qualificationList.postValue(result)
        }
    }

    fun getMajorFieldList() {
        repository.majorFieldList { result ->
            _majorFieldList.postValue(result)
        }
    }

    fun getMiddleFieldList(field: String) {
        repository.middleFieldList(field) { result ->
            _middleFieldList.postValue(result)
        }
    }
}