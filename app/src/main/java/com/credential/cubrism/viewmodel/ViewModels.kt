package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.adapter.Goals


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