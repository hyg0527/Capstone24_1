package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.Goals

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
//
//class TodoViewModel : ViewModel() {
//    val itemList: MutableLiveData<List<TodayData>> = MutableLiveData()
//
//    // 아이템 상태 업데이트
//    fun updateTodo(position: Int, isChecked: Boolean) {
//        val currentList = itemList.value.orEmpty().toMutableList()
//        currentList[position].todayCheck = isChecked
//        itemList.value = currentList
//    }
//}

class GoalListViewModel : ViewModel() {
    private val _goalList = MutableLiveData<ArrayList<Goals>>(arrayListOf())
    val goalList: LiveData<ArrayList<Goals>> get() = _goalList

    init {
        addList(Goals("목표 1입니다.", "0시간 0분", 1))
    }
    fun addList(value: Goals) {
        _goalList.value?.add(value)
        _goalList.value = _goalList.value
    }
}

class DDayViewModel : ViewModel() {
    private val _pairStringLiveData = MutableLiveData<Pair<String, String>>()
    val pairStringLiveData: LiveData<Pair<String, String>> get() = _pairStringLiveData

    // Pair<String, String> 값을 설정하는 함수
    fun setPairString(pair: Pair<String, String>) {
        _pairStringLiveData.value = pair
    }
}

class TitleViewModel : ViewModel() {
    // 초기값을 보관하는 변수
    private val _initialValue = "스터디 환영글입니다."
    private val _editTextValue = MutableLiveData<String>()

    // LiveData를 통해 초기값을 전달하는 메서드
    val editTextValue: LiveData<String> = _editTextValue

    init {
        // 초기값을 LiveData에 설정
        setEditTextValue(_initialValue)
    }

    // EditText의 값을 저장하는 메서드
    fun setEditTextValue(value: String) {
        _editTextValue.value = value
    }
}