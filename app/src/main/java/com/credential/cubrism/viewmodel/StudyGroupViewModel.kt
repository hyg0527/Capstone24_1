package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.model.dto.MessageDto
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

    private val _studyGroupInfo = MutableLiveData<StudyGroupInfoDto>()
    val studyGroupInfo: LiveData<StudyGroupInfoDto> = _studyGroupInfo

    private val _requestJoin = MutableLiveData<MessageDto>()
    val requestJoin: LiveData<MessageDto> = _requestJoin

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getStudyGroupList(page: Int, limit: Int, recruiting: Boolean, refresh: Boolean = false) {
        repository.studyGroupList(page, limit, recruiting) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    if (refresh) {
                        _studyGroupList.postValue(result.data.studyGroupList)
                    } else {
                        setLoading(true)
                        _studyGroupList.postValue(_studyGroupList.value.orEmpty() + result.data.studyGroupList)
                    }
                    _page.postValue(result.data.page)
                }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun getStudyGroupInfo(groupId: Int) {
        repository.studyGroupInfo(groupId) { result ->
            handleResult(result, _studyGroupInfo, _errorMessage)
        }
    }

    fun requestJoin(groupId: Int) {
        repository.requestJoin(groupId) { result ->
            handleResult(result, _requestJoin, _errorMessage)
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun <T> handleResult(result: ResultUtil<T>, successLiveData: MutableLiveData<T>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}