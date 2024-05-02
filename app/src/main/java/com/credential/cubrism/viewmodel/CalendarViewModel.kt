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
        addDateMonth(CalMonth(startDate="2024-05-02T00:00", endDate="2024-05-02T00:00",
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

//    private fun checkFormat(value: CalMonth): CalMonth { // 형식 체크(로컬데이터 형식으로 변환 후 반환)
//        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
//        val dateFormatterOutput = SimpleDateFormat("yyyy - MM - dd a hh:mm", Locale.KOREA)
//        val dateFormatterOutputAllDay = SimpleDateFormat("yyyy - MM - dd 종일", Locale.getDefault())
//
//        try { // startDate를 dateFormatter로 파싱하여 오류가 없으면 value 그대로 반환
//            dateFormatterOutput.parse(value.startDate ?: "")
//            return value
//        }
//        catch (e: ParseException) { // ParseException이 발생 하면 format 변경
//            var startDate = ""; var endDate = ""
//
//            if (value.endDate == null) {
//                startDate = dateFormatterOutputAllDay.format(dateFormatter.parse(value.startDate ?: "") ?: "")
//                endDate = dateFormatterOutputAllDay.format(dateFormatter.parse(value.startDate ?: "") ?: "")
//            }
//            else {
//                startDate = dateFormatterOutput.format(dateFormatter.parse(value.startDate ?: "") ?: "")
//                endDate = dateFormatterOutput.format(dateFormatter.parse(value.endDate ?: "") ?: "")
//            }
//
//            return value.copy(startDate = startDate, endDate = endDate)
//        }
//    }
}