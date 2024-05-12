package com.credential.cubrism.model.repository

import com.credential.cubrism.model.api.StudyGroupApi
import com.credential.cubrism.model.dto.ChatResponseDto
import com.credential.cubrism.model.dto.DDayDto
import com.credential.cubrism.model.dto.GoalsDto
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.StudyGroupAddGoalDto
import com.credential.cubrism.model.dto.StudyGroupCreateDto
import com.credential.cubrism.model.dto.StudyGroupEnterDto
import com.credential.cubrism.model.dto.StudyGroupInfoDto
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto
import com.credential.cubrism.model.dto.StudyGroupListDto
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.utils.NetworkUtil.handleResponse
import com.credential.cubrism.model.utils.ResultUtil
import java.util.UUID

class StudyGroupRepository {
    private val studyGroupApi: StudyGroupApi = RetrofitClient.getRetrofit()?.create(StudyGroupApi::class.java)!!
    private val studyGroupApiAuth: StudyGroupApi = RetrofitClient.getRetrofitWithAuth()?.create(StudyGroupApi::class.java)!!

    fun createStudyGroup(studyGroupCreateDto: StudyGroupCreateDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.createStudyGroup(studyGroupCreateDto), callback)
    }

    fun studyGroupList(page: Int, limit: Int, recruiting: Boolean, callback: (ResultUtil<StudyGroupListDto>) -> Unit) {
        handleResponse(studyGroupApi.getStudyGroupList(page, limit, recruiting), callback)
    }

    fun myStudyGroupList(callback: (ResultUtil<List<GroupList>>) -> Unit) {
        handleResponse(studyGroupApiAuth.getMyStudyGroupList(), callback)
    }

    fun studyGroupInfo(groupId: Int, callback: (ResultUtil<StudyGroupInfoDto>) -> Unit) {
        handleResponse(studyGroupApi.getStudyGroupInfo(groupId), callback)
    }

    fun requestJoin(groupId: Int, callback: (ResultUtil<MessageDto> ) -> Unit) {
        handleResponse(studyGroupApiAuth.requestJoin(groupId), callback)
    }

    fun joinRequestList(callback: (ResultUtil<List<StudyGroupJoinListDto>>) -> Unit) {
        handleResponse(studyGroupApiAuth.getJoinRequestList(), callback)
    }

    fun acceptJoinRequest(memberId: UUID, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.acceptJoinRequest(memberId), callback)
    }

    fun denyJoinRequest(memberId: UUID, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.denyJoinRequest(memberId), callback)
    }

    fun joinReceiveList(groupId: Int, callback: (ResultUtil<List<StudyGroupJoinReceiveListDto>>) -> Unit) {
        handleResponse(studyGroupApiAuth.getJoinReceiveList(groupId), callback)
    }

    fun addGoal(studyGroupAddGoalDto: StudyGroupAddGoalDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.addGoal(studyGroupAddGoalDto), callback)
    }

    fun deleteGoal(goalId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.deleteGoal(goalId), callback)
    }

    fun completeGoal(goalId: Int, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.completeGoal(goalId), callback)
    }

    fun goalList(groupId: Int, callback: (ResultUtil<List<GoalsDto>>) -> Unit) {
        handleResponse(studyGroupApiAuth.getGoalList(groupId), callback)
    }

    fun setDday(dDayDto: DDayDto, callback: (ResultUtil<MessageDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.setDday(dDayDto), callback)
    }

    fun studyGroupEnterData(groupId: Int, callback: (ResultUtil<StudyGroupEnterDto>) -> Unit) {
        handleResponse(studyGroupApiAuth.getStudyGroupEnterData(groupId), callback)
    }

    fun getChatList(studygroupId: Int, callback: (ResultUtil<List<ChatResponseDto>>) -> Unit) {
        handleResponse(studyGroupApiAuth.getChatList(studygroupId), callback)
    }
}