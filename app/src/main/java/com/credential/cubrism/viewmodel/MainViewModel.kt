package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

enum class MainFragmentType(val tag: String) {
    HOME("home"),
    STUDY("study"),
    CALENDAR("schedule"),
    QUALIFICATION("qualification")
}

class MainViewModel : ViewModel() {
    private val _currentFragmentType = MutableLiveData(MainFragmentType.HOME)
    val currentFragmentType: LiveData<MainFragmentType> = _currentFragmentType

    fun setCurrentFragment(item: MeowBottomNavigation.Model): Boolean {
        val pageType = getPageType(item)
        changeCurrentFragmentType(pageType)

        return true
    }

    private fun getPageType(item: MeowBottomNavigation.Model): MainFragmentType {
        return when (item.id) {
            1 -> MainFragmentType.HOME
            2 -> MainFragmentType.STUDY
            3 -> MainFragmentType.CALENDAR
            4 -> MainFragmentType.QUALIFICATION
            else -> MainFragmentType.HOME
        }
    }

    private fun changeCurrentFragmentType(fragmentType: MainFragmentType) {
        if (currentFragmentType.value == fragmentType)
            return

        _currentFragmentType.value = fragmentType
    }
}