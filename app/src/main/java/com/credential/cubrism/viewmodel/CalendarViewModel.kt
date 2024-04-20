package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.CalMonth

class CalendarViewModel : ViewModel() {
    private val _calMonthList = MutableLiveData<ArrayList<CalMonth>>(arrayListOf())
    val calMonthList: LiveData<ArrayList<CalMonth>> get() = _calMonthList

    init { // 샘플 일정 생성
        addDateMonth(CalMonth(title="일정1", startTime="2024 - 04 - 10 오전 01:00",
            endTime="2024 - 04 - 10 오전 02:00", info="일정1 입니다.", isFullTime=false))
        addDateMonth(CalMonth(title="일정2", startTime="2024 - 04 - 10 오전 01:00",
            endTime="2024 - 04 - 10 오전 02:00", info="일정2 입니다.", isFullTime=false))
        addDateMonth(CalMonth(title="ios 중간 시험", startTime="2024 - 04 - 22 오후 01:30",
            endTime="2024 - 04 - 22 오후 03:00", info="ios 시험일", isFullTime=false))
//        addDateMonth(CalMonth(title="예비군 다음날", startTime="2024 - 04 - 17 종일",
//            endTime="2024 - 04 - 17 종일", info="예비군 끝났다!!", isFullTime=true))
//        addDateMonth(CalMonth(title="예비군 다음날2", startTime="2024 - 04 - 17 종일",
//            endTime="2024 - 04 - 17 종일", info="예비군 끝났다!!", isFullTime=true))
//        addDateMonth(CalMonth(title="예비군 다음날3", startTime="2024 - 04 - 17 종일",
//            endTime="2024 - 04 - 17 종일", info="예비군 끝났다!!", isFullTime=true))
    }

    fun addDateMonth(value: CalMonth) { // 추가
        _calMonthList.value?.add(value)
        _calMonthList.value = _calMonthList.value
    }

    fun deleteDateMonth(value: CalMonth) { // 삭제
        _calMonthList.value?.remove(value)
        _calMonthList.value = _calMonthList.value
    }
}