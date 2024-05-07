package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.repository.ChatRepository
import com.credential.cubrism.model.dto.ChatResponseDto
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val _chatList = MutableLiveData<MutableList<ChatResponseDto>>()
    val chatList: LiveData<MutableList<ChatResponseDto>> = _chatList

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getChatList(studygroupId: Int) {
        chatRepository.getChatList(studygroupId) { result ->
            when (result) {
                is ResultUtil.Success -> { _chatList.postValue(result.data.toMutableList()) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun addChat(chat: ChatResponseDto) {
        _chatList.value?.add(chat)
        _chatList.postValue(_chatList.value)
    }
}