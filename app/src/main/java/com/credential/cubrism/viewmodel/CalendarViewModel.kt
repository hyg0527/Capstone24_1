package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.CalMonth

class CalendarViewModel : ViewModel() {
    private val _calMonthList = MutableLiveData<ArrayList<CalMonth>>(arrayListOf())
    val calMonthList: LiveData<ArrayList<CalMonth>> get() = _calMonthList

    init { // 샘플 일정 생성
        addDateMonth(CalMonth(startDate="2024-03-31T17:00", endDate="2024-04-01T17:00",
            title="일정1", content="내용1", allDay=false))
        addDateMonth(CalMonth(startDate="2024-04-03T00:00", endDate="2024-04-03T00:00",
            title="일정2", content="내용2", allDay=true))
        addDateMonth(CalMonth(startDate="2024-05-03T00:00", endDate="2024-05-03T00:00",
            title="일정3", content="내용3", allDay=true))
        /*
        { "scheduleId": 1, "startDate": "2024-03-31T17:00", "endDate": "2024-04-01T17:00",
        "title": "일정1", "content": "내용1", "allDay": false }
        { "startDate": "2024-03-31T17:00", "endDate": "2024-04-01T17:00",
        "title": "일정1", "content": "내용1", "allDay": false }
        */
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