package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class StudyFragmentType(val tag: String) {
    HOME("home"),
    LEARN_RATE("learn_rate"),
    CHAT("chat"),
}

class StudyViewModel : ViewModel() {
    private val _currentFragmentType = MutableLiveData(StudyFragmentType.HOME)
    val currentFragmentType: LiveData<StudyFragmentType> = _currentFragmentType

    fun setCurrentFragment(item: Int): Boolean {
        val pageType = getPageType(item)
        changeCurrentFragmentType(pageType)

        return true
    }

    private fun getPageType(item: Int): StudyFragmentType {
        return when (item) {
            0 -> StudyFragmentType.HOME
            1 -> StudyFragmentType.LEARN_RATE
            2 -> StudyFragmentType.CHAT
            else -> StudyFragmentType.HOME
        }
    }

    private fun changeCurrentFragmentType(fragmentType: StudyFragmentType) {
        if (currentFragmentType.value == fragmentType)
            return

        _currentFragmentType.value = fragmentType
    }
}