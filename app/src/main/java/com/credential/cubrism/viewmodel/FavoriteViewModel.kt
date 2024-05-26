package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.FavoriteAddDto
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.repository.FavoriteRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {
    private val _addFavorite = MutableLiveData<MessageDto>()
    val addFavorite: LiveData<MessageDto> = _addFavorite

    private val _deleteFavorite = MutableLiveData<MessageDto>()
    val deleteFavorite: LiveData<MessageDto> = _deleteFavorite

    private val _favoriteList = MutableLiveData<List<FavoriteListDto>>()
    val favoriteList: LiveData<List<FavoriteListDto>> = _favoriteList

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun addFavorite(favoriteAddDto: FavoriteAddDto) {
        favoriteRepository.addFavorite(favoriteAddDto) { result ->
            handleResult(result, _addFavorite, _errorMessage)
        }
    }

    fun deleteFavorite(favoriteId: Int) {
        favoriteRepository.deleteFavorite(favoriteId) { result ->
            handleResult(result, _deleteFavorite, _errorMessage)
        }
    }

    fun getFavoriteList() {
        favoriteRepository.getFavoriteList { result ->
            when (result) {
                is ResultUtil.Success -> { _favoriteList.postValue(result.data) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    private fun handleResult(result: ResultUtil<MessageDto>, successLiveData: MutableLiveData<MessageDto>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}