package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.ChatResponseDto
import com.credential.cubrism.model.dto.DDayDto
import com.credential.cubrism.model.dto.GoalsDto
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.PageDto
import com.credential.cubrism.model.dto.StudyGroupAddGoalDto
import com.credential.cubrism.model.dto.StudyGroupCreateDto
import com.credential.cubrism.model.dto.StudyGroupEnterDto
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitDto
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitListDto
import com.credential.cubrism.model.dto.StudyGroupInfoDto
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event
import java.util.UUID

class StudyGroupViewModel(private val repository: StudyGroupRepository) : ViewModel() {
    private val _groupId = MutableLiveData<Int>()
    val groupId: LiveData<Int> = _groupId

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    private val _page = MutableLiveData<PageDto>()
    val page: LiveData<PageDto> = _page

    private val _createGroup = MutableLiveData<MessageDto>()
    val createGroup: LiveData<MessageDto> = _createGroup

    private val _studyGroupList = MutableLiveData<List<GroupList>>()
    val studyGroupList: LiveData<List<GroupList>> = _studyGroupList

    private val _studyGroupMyList = MutableLiveData<List<GroupList>>()
    val studyGroupMyList: LiveData<List<GroupList>> = _studyGroupMyList

    private val _studyGroupInfo = MutableLiveData<StudyGroupInfoDto>()
    val studyGroupInfo: LiveData<StudyGroupInfoDto> = _studyGroupInfo

    private val _requestJoin = MutableLiveData<MessageDto>()
    val requestJoin: LiveData<MessageDto> = _requestJoin

    private val _cancelJoin = MutableLiveData<MessageDto>()
    val cancelJoin: LiveData<MessageDto> = _cancelJoin

    private val _joinRequestList = MutableLiveData<List<StudyGroupJoinListDto>>()
    val joinRequestList: LiveData<List<StudyGroupJoinListDto>> = _joinRequestList

    private val _acceptJoinRequest = MutableLiveData<MessageDto>()
    val acceptJoinRequest: LiveData<MessageDto> = _acceptJoinRequest

    private val _denyJoinRequest = MutableLiveData<MessageDto>()
    val denyJoinRequest: LiveData<MessageDto> = _denyJoinRequest

    private val _joinReceiveList = MutableLiveData<List<StudyGroupJoinReceiveListDto>>()
    val joinReceiveList: LiveData<List<StudyGroupJoinReceiveListDto>> = _joinReceiveList

    private val _addGoal = MutableLiveData<MessageDto>()
    val addGoal: LiveData<MessageDto> = _addGoal

    private val _deleteGoal = MutableLiveData<MessageDto>()
    val deleteGoal: LiveData<MessageDto> = _deleteGoal

    private val _goalList = MutableLiveData<List<GoalsDto>>()
    val goalList: LiveData<List<GoalsDto>> = _goalList

    private val _submitGoal = MutableLiveData<MessageDto>()
    val submitGoal: LiveData<MessageDto> = _submitGoal

    private val _acceptGoalSubmit = MutableLiveData<MessageDto>()
    val acceptGoalSubmit: LiveData<MessageDto> = _acceptGoalSubmit

    private val _denyGoalSubmit = MutableLiveData<MessageDto>()
    val denyGoalSubmit: LiveData<MessageDto> = _denyGoalSubmit

    private val _goalSubmitList = MutableLiveData<List<StudyGroupGoalSubmitListDto>>()
    val goalSubmitList: LiveData<List<StudyGroupGoalSubmitListDto>> = _goalSubmitList

    private val _setDday = MutableLiveData<MessageDto>()
    val setDday: LiveData<MessageDto> = _setDday

    private val _getDday = MutableLiveData<DDayDto>()
    val getDday: LiveData<DDayDto> = _getDday

    private val _studyGroupEnterData = MutableLiveData<StudyGroupEnterDto>()
    val studyGroupEnterData: LiveData<StudyGroupEnterDto> = _studyGroupEnterData

    private val _chatList = MutableLiveData<MutableList<ChatResponseDto>>()
    val chatList: LiveData<MutableList<ChatResponseDto>> = _chatList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun setGroupId(groupId: Int) {
        _groupId.value = groupId
    }

    fun setIsAdmin(isAdmin: Boolean) {
        _isAdmin.value = isAdmin
    }

    fun createStudyGroup(studyGroupCreateDto: StudyGroupCreateDto) {
        repository.createStudyGroup(studyGroupCreateDto) { result ->
            handleResult(result, _createGroup, _errorMessage)
        }
    }

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

    fun getMyStudyGroupList() {
        repository.myStudyGroupList { result ->
            handleResult(result, _studyGroupMyList, _errorMessage)
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

    fun cancelJoin(memberId: String) {
        repository.cancelJoin(memberId) { result ->
            handleResult(result, _cancelJoin, _errorMessage)
        }
    }

    fun getStudyGroupJoinRequestList() {
        repository.joinRequestList { result ->
            handleResult(result, _joinRequestList, _errorMessage)
        }
    }

    fun acceptJoinRequest(memberId: UUID) {
        repository.acceptJoinRequest(memberId) { result ->
            handleResult(result, _acceptJoinRequest, _errorMessage)
        }
    }

    fun denyJoinRequest(memberId: UUID) {
        repository.denyJoinRequest(memberId) { result ->
            handleResult(result, _denyJoinRequest, _errorMessage)
        }
    }

    fun getStudyGroupJoinReceiveList(groupId: Int) {
        repository.joinReceiveList(groupId) { result ->
            handleResult(result, _joinReceiveList, _errorMessage)
        }
    }

    fun addGoal(studyGroupAddGoalDto: StudyGroupAddGoalDto) {
        repository.addGoal(studyGroupAddGoalDto) { result ->
            handleResult(result, _addGoal, _errorMessage)
        }
    }

    fun deleteGoal(goalId: Int) {
        repository.deleteGoal(goalId) { result ->
            handleResult(result, _deleteGoal, _errorMessage)
        }
    }

    fun getGoalList(groupId: Int) {
        repository.goalList(groupId) { result ->
            handleResult(result, _goalList, _errorMessage)
        }
    }

    fun submitGoal(studyGroupGoalSubmitDto: StudyGroupGoalSubmitDto) {
        repository.submitGoal(studyGroupGoalSubmitDto) { result ->
            handleResult(result, _submitGoal, _errorMessage)
        }
    }

    fun acceptGoalSubmit(userGoalId: Int) {
        repository.acceptGoalSubmit(userGoalId) { result ->
            handleResult(result, _acceptGoalSubmit, _errorMessage)
        }
    }

    fun denyGoalSubmit(userGoalId: Int) {
        repository.denyGoalSubmit(userGoalId) { result ->
            handleResult(result, _denyGoalSubmit, _errorMessage)
        }
    }

    fun getGoalSubmitList(groupId: Int) {
        repository.goalSubmitList(groupId) { result ->
            handleResult(result, _goalSubmitList, _errorMessage)
        }
    }

    fun setDday(dDayDto: DDayDto) {
        repository.setDday(dDayDto) { result ->
            handleResult(result, _setDday, _errorMessage)
        }
    }

    fun getDday(groupId: Int) {
        repository.getDday(groupId) { result ->
            handleResult(result, _getDday, _errorMessage)
        }
    }

    fun getStudyGroupEnterData(groupId: Int) {
        repository.studyGroupEnterData(groupId) { result ->
            handleResult(result, _studyGroupEnterData, _errorMessage)
        }
    }

    fun getChatList(studygroupId: Int) {
        repository.getChatList(studygroupId) { result ->
            when (result) {
                is ResultUtil.Success -> { _chatList.postValue(result.data.toMutableList()) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun addChat(chat: ChatResponseDto) {
        _chatList.value?.add(chat)
        _chatList.postValue(_chatList.value)
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