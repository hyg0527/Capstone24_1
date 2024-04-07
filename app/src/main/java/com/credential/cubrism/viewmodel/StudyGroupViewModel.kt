package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.model.dto.PageDto
import com.credential.cubrism.model.dto.StudyGroupInfoDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class StudyGroupViewModel(private val repository: StudyGroupRepository) : ViewModel() {
    private val _page = MutableLiveData<PageDto>()
    val page: LiveData<PageDto> = _page

    private val _studyGroupList = MutableLiveData<List<GroupList>>()
    val studyGroupList: LiveData<List<GroupList>> = _studyGroupList

    private val _studyGroupInfo = MutableLiveData<ResultUtil<StudyGroupInfoDto>>()
    val studyGroupInfo: LiveData<ResultUtil<StudyGroupInfoDto>> = _studyGroupInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getStudyGroupList(page: Int, limit: Int, recruiting: Boolean, refresh: Boolean = false) {
        _isLoading.value = true
        repository.studyGroupList(page, limit, recruiting) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    if (refresh) {
                        _studyGroupList.postValue(result.data.studyGroupList)
                    } else {
                        _studyGroupList.postValue(_studyGroupList.value.orEmpty() + result.data.studyGroupList)
                    }
                    _page.postValue(result.data.page)
                }
                is ResultUtil.Error -> {
                    _errorMessage.postValue(Event(result.error))
                }
                is ResultUtil.NetworkError -> {
                    _errorMessage.postValue(Event("네트워크 오류가 발생했습니다."))
                }
            }
            _isLoading.value = false
        }
    }

    fun getStudyGroupInfo(groupId: Int) {
        repository.studyGroupInfo(groupId) { result ->
            _studyGroupInfo.postValue(result)
        }
    }
}