package com.credential.cubrism.model.api

import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.StudyGroupCreateDto
import com.credential.cubrism.model.dto.StudyGroupInfoDto
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.model.dto.StudyGroupListDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyGroupApi {
    // 스터디 그룹 생성
    @POST("/studygroup")
    fun createStudyGroup(@Body studyGroupCreateDto: StudyGroupCreateDto): Call<MessageDto>

    // 스터디 그룹 삭제
//    @DELETE("/studygroup/{groupId}")
//    fun deleteStudyGroup(@Path("groupId") groupId: Int): Call<MessageDto>

    // 스터디 그룹 수정
//    @PUT("/studygroup/{groupId}")
//    fun updateStudyGroup(@Path("groupId") groupId: Int, @Body studyGroupUpdateDto: StudyGroupUpdateDto): Call<MessageDto>

    // 스터디 그룹 정보
    @GET("/studygroup/{groupId}")
    fun getStudyGroupInfo(@Path("groupId") groupId: Int): Call<StudyGroupInfoDto>

    // 스터디 그룹 가입 신청 목록
    @GET("/studygroup/join/list")
    fun getJoinRequestList(): Call<List<StudyGroupJoinListDto>>

    // 스터디 그룹 가입 요청
    @POST("/studygroup/join/{groupId}")
    fun requestJoin(@Path("groupId") groupId: Int): Call<MessageDto>

    // 가입 요청 목록
//    @GET("/studygroup/join")
//    fun getJoinRequest(): Call<StudyGroupJoinListDto>

    // 스터디 그룹 가입 승인
//    @POST("/studygroup/join/{memberId}")
//    fun approveJoinRequest(@Path("memberId") memberId: UUID): Call<MessageDto>

    // 스터디 그룹 가입 거절
//    @DELETE("/studygroup/join/{memberId}")
//    fun rejectJoinRequest(@Path("memberId") memberId: UUID): Call<MessageDto>

    // 스터디 그룹 탈퇴
//    @DELETE("/studygroup/leave/{groupId}")
//    fun leaveStudyGroup(@Path("groupId") groupId: Int): Call<MessageDto>

    // 스터디 그룹 목록
    @GET("/studygroups")
    fun getStudyGroupList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("recruiting") recruiting: Boolean
    ): Call<StudyGroupListDto>

    // 내 스터디 그룹 목록
//    @GET("/studygroups/my")
//    fun getMyStudyGroupList(
//        @Query("page") page: Int,
//        @Query("limit") limit: Int
//    ): Call<StudyGroupListDto>

    // 스터디 그룹 목표 추가
//    @POST("/studygroup/goal")
//    fun addGoal(@Body studyGroupAddGoalDto: StudyGroupAddGoalDto): Call<MessageDto>

    // 스터디 그룹 목표 삭제
//    @DELETE("/studygroup/goal/{goalId}")
//    fun deleteGoal(@Path("goalId") goalId: Int): Call<MessageDto>

    // 스터디 그룹 목표 수정
//    @PUT("/studygroup/goal/{goalId}")
//    fun updateStudyGroupGoal(@Path("goalId") goalId: Int, @Body studyGroupUpdateGoalDto: StudyGroupUpdateGoalDto): Call<MessageDto>

    // 스터디 그룹 목표 정보
//    @GET("/studygroup/goal/{goalId}")
//    fun getGoalInfo(@Path("goalId") goalId: Int): Call<StudyGroupGoalInfo>
}