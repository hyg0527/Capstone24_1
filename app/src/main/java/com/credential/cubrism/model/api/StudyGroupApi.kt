package com.credential.cubrism.model.api

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
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface StudyGroupApi {
    // 스터디 그룹 생성
    @POST("/studygroup")
    fun createStudyGroup(@Body studyGroupCreateDto: StudyGroupCreateDto): Call<MessageDto>

    // 스터디 그룹 목록
    @GET("/studygroups")
    fun getStudyGroupList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("recruiting") recruiting: Boolean
    ): Call<StudyGroupListDto>

    // 내가 가입한 스터디 그룹 목록
    @GET("/studygroups/my")
    fun getMyStudyGroupList(): Call<List<GroupList>>

    // 스터디 그룹 정보
    @GET("/studygroup/{groupId}")
    fun getStudyGroupInfo(@Path("groupId") groupId: Int): Call<StudyGroupInfoDto>

    // 스터디 그룹 가입 신청
    @POST("/studygroup/join/request/{groupId}")
    fun requestJoin(@Path("groupId") groupId: Int): Call<MessageDto>

    // 스터디 그룹 가입 신청 취소
    @DELETE("/studygroup/join/request/{memberId}")
    fun cancelJoin(@Path("memberId") memberId: String): Call<MessageDto>

    // 내가 신청한 스터디 그룹 목록
    @GET("/studygroup/join/requests")
    fun getJoinRequestList(): Call<List<StudyGroupJoinListDto>>

    // 스터디 그룹 가입 승인
    @PUT("/studygroup/join/receive/{memberId}")
    fun acceptJoinRequest(@Path("memberId") memberId: UUID): Call<MessageDto>

    // 스터디 그룹 가입 거절
    @DELETE("/studygroup/join/receive/{memberId}")
    fun denyJoinRequest(@Path("memberId") memberId: UUID): Call<MessageDto>

    // 해당 스터디 그룹 가입 신청 목록
    @GET("/studygroup/join/receives/{groupId}")
    fun getJoinReceiveList(@Path("groupId") groupId: Int): Call<List<StudyGroupJoinReceiveListDto>>

    // 스터디 그룹 목표 추가
    @POST("/studygroup/goal")
    fun addGoal(@Body studyGroupAddGoalDto: StudyGroupAddGoalDto): Call<MessageDto>

    // 스터디 그룹 목표 삭제
    @DELETE("/studygroup/goal/{goalId}")
    fun deleteGoal(@Path("goalId") goalId: Int): Call<MessageDto>

    // 스터디 그룹 목표 완료
    @PUT("/studygroup/goal/{goalId}")
    fun completeGoal(@Path("goalId") goalId: Int): Call<MessageDto>

    // 스터디 그룹 목표 목록
    @GET("/studygroup/{groupId}/goals")
    fun getGoalList(@Path("groupId") groupId: Int): Call<List<GoalsDto>>

    // 스터디 그룹 D-Day 설정
    @POST("/studygroup/dday")
    fun setDday(@Body dDayDto: DDayDto): Call<MessageDto>

    // 스터디 그룹 입장 데이터
    @GET("/studygroup/{groupId}/enter")
    fun getStudyGroupEnterData(@Path("groupId") groupId: Int): Call<StudyGroupEnterDto>

    // 채팅
    @GET("/studygroup/{studygroupId}/chats")
    fun getChatList(@Path("studygroupId") studygroupId: Int): Call<List<ChatResponseDto>>
}