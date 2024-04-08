package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val _addPost = MutableLiveData<ResultUtil<PostAddDto>>()
    val addPost: LiveData<ResultUtil<PostAddDto>> = _addPost

    private val _postList = MutableLiveData<List<PostList>>()
    val postList: LiveData<List<PostList>> = _postList

    private val _postView = MutableLiveData<ResultUtil<PostViewDto>>()
    val postView: LiveData<ResultUtil<PostViewDto>> = _postView

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshed = MutableLiveData<Boolean>()
    val isRefreshed: LiveData<Boolean> = _isRefreshed

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun addPost(postAddDto: PostAddDto) {
        postRepository.addPost(postAddDto) { result ->
            _addPost.postValue(result)
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

    fun getPostView(postId: Int) {
        postRepository.getPostView(postId) { result ->
            _postView.postValue(result)
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}