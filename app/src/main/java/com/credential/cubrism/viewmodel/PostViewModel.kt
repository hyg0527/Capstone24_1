package com.credential.cubrism.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.PageDto
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostList
import com.credential.cubrism.model.dto.PostViewDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _page = MutableLiveData<PageDto>()
    val page: LiveData<PageDto> = _page

    private val _addPost = MutableLiveData<PostAddDto>()
    val addPost: LiveData<PostAddDto> = _addPost

    private val _postView = MutableLiveData<PostViewDto>()
    val postView: LiveData<PostViewDto> = _postView

    private val _postList = MutableLiveData<List<PostList>>()
    val postList: LiveData<List<PostList>> = _postList

    private val _addComment = MutableLiveData<CommentAddDto>()
    val addComment: LiveData<CommentAddDto> = _addComment

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshed = MutableLiveData<Boolean>()
    val isRefreshed: LiveData<Boolean> = _isRefreshed

    private val _clickedItem = MutableLiveData<String>()
    val clickedItem: LiveData<String> = _clickedItem

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun addPost(postAddDto: PostAddDto) {
        postRepository.addPost(postAddDto) { result ->
            when (result) {
                is ResultUtil.Success -> { _addPost.postValue(postAddDto) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun getPostView(postId: Int) {
        postRepository.getPostView(postId) { result ->
            when (result) {
                is ResultUtil.Success -> { _postView.postValue(result.data) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun getPostList(boardId: Int, page: Int, limit: Int, searchQuery: String?, refresh: Boolean = false) {
        postRepository.getPostList(boardId, page, limit, searchQuery) { result ->
            _isRefreshed.value = refresh

            when (result) {
                is ResultUtil.Success -> {
                    if (refresh) {
                        _postList.postValue(result.data.postList)
                    } else {
                        setLoading(true)
                        _postList.postValue(_postList.value.orEmpty() + result.data.postList)
                    }
                    _page.postValue(result.data.page)
                }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun getFavoritePostList(boardId: Int, page: Int, limit: Int, refresh: Boolean = false) {
        postRepository.getFavoritePostList(boardId, page, limit) { result ->
            _isRefreshed.value = refresh

            when (result) {
                is ResultUtil.Success -> {
                    if (refresh) {
                        _postList.postValue(result.data.postList)
                    } else {
                        setLoading(true)
                        _postList.postValue(_postList.value.orEmpty() + result.data.postList)
                    }
                    _page.postValue(result.data.page)
                }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun addComment(commentAddDto: CommentAddDto) {
        postRepository.addComment(commentAddDto) { result ->
            when (result) {
                is ResultUtil.Success -> { _addComment.postValue(commentAddDto) }
                is ResultUtil.Error -> { _errorMessage.postValue(Event(result.error)) }
                is ResultUtil.NetworkError -> { _errorMessage.postValue(Event("네트워크 오류가 발생했습니다.")) }
            }
        }
    }

    fun setClickedItem(value: String) {
        _clickedItem.value = value
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}