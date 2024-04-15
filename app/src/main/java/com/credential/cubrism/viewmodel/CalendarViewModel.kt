package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.CalMonth

class CalendarViewModel : ViewModel() {
    private val _calMonthList = MutableLiveData<ArrayList<CalMonth>>(arrayListOf())
    val calMonthList: LiveData<ArrayList<CalMonth>> get() = _calMonthList // 읽기만 가능(get)

    init {
        addDateMonth(CalMonth(title="일정1", startTime="2024 - 04 - 10 오전 01:00",
            endTime="2024 - 04 - 10 오전 02:00", info="일정1 입니다.", isFullTime=false))
        addDateMonth(CalMonth(title="일정2", startTime="2024 - 04 - 10 오전 01:00",
            endTime="2024 - 04 - 10 오전 02:00", info="일정2 입니다.", isFullTime=false))
        addDateMonth(CalMonth(title="ios 중간고사", startTime="2024 - 04 - 22 오후 01:30",
            endTime="2024 - 04 - 22 오후 03:00", info="ios 시험일", isFullTime=false))
//        addDateMonth(CalMonth(title="예비군 하루전...", startTime="2024 - 04 - 15 오후 01:30",
//            endTime="2024 - 04 - 15 오후 03:00", info="예비군 하루전이다", isFullTime=false))
    }

    fun addDateMonth(value: CalMonth) { // 추가
        _calMonthList.value?.add(value)
        _calMonthList.value = _calMonthList.value // 옵서버 에게 변경 사항을 알림
    }

    fun deleteDateMonth(value: CalMonth) { // 삭제
        _calMonthList.value?.remove(value)
        _calMonthList.value = _calMonthList.value
    }
}