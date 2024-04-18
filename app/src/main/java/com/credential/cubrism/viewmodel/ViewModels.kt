package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.Goals


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
        addList(Goals(1, "목표 1입니다.", 1))
    }
    fun addList(value: Goals) {
        _goalList.value?.add(value)
        _goalList.value = _goalList.value
    }
}

class DDayViewModel : ViewModel() {
    private val _pairStringLiveData = MutableLiveData<Pair<String, Int>>()
    val pairStringLiveData: LiveData<Pair<String, Int>> get() = _pairStringLiveData

    init {
        setDDay(Pair("정보처리기사 시험", 11))
    }
    // Pair<String, String> 값을 설정하는 함수
    fun setDDay(pair: Pair<String, Int>) {
        _pairStringLiveData.value = pair
    }
}