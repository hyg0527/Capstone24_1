package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.Goals
import com.credential.cubrism.view.adapter.myCertData


class GoalListViewModel : ViewModel() {
    private val _goalList = MutableLiveData<ArrayList<Goals>>(arrayListOf())
    val goalList: LiveData<ArrayList<Goals>> get() = _goalList

    init {
        addList(Goals(1, "목표 1입니다.", 1))
        addList(Goals(2, "목표 2입니다.", 2))
        addList(Goals(3, "목표 3입니다.", 3))
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

class CertListViewModel : ViewModel() {
    private val _certList = MutableLiveData<ArrayList<myCertData>>(arrayListOf())
    val certList: LiveData<ArrayList<myCertData>> get() = _certList

    init {
        addList(myCertData("정보처리기사", 1))
        addList(myCertData("한식조리기능사", 2))
        addList(myCertData("직업상담사1급", 3))
    }
    fun addList(value: myCertData) {
        _certList.value?.add(value)
        _certList.value = _certList.value
    }
}