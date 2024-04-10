package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.view.utils.FragmentType
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainViewModel : ViewModel() {
    private val _currentFragmentType = MutableLiveData(FragmentType.HOME)
    val currentFragmentType: LiveData<FragmentType> = _currentFragmentType

    fun setCurrentFragment(item: MeowBottomNavigation.Model): Boolean {
        val pageType = getPageType(item)
        changeCurrentFragmentType(pageType)

        return true
    }

    private fun getPageType(item: MeowBottomNavigation.Model): FragmentType {
        return when (item.id) {
            1 -> FragmentType.HOME
            2 -> FragmentType.STUDY
            3 -> FragmentType.CALENDAR
            4 -> FragmentType.QUALIFICATION
            else -> FragmentType.HOME
        }
    }

    private fun changeCurrentFragmentType(fragmentType: FragmentType) {
        if (currentFragmentType.value == fragmentType)
            return

        _currentFragmentType.value = fragmentType
    }
}