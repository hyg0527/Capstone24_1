package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.CommentUpdateDto
import com.credential.cubrism.model.dto.MessageDto
import com.credential.cubrism.model.dto.PageDto
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostList
import com.credential.cubrism.model.dto.PostMyList
import com.credential.cubrism.model.dto.PostUpdateDto
import com.credential.cubrism.model.dto.PostViewDto
import com.credential.cubrism.model.dto.ReplyAddDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.utils.Event

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _page = MutableLiveData<PageDto>()
    val page: LiveData<PageDto> = _page

    private val _addPost = MutableLiveData<MessageDto>()
    val addPost: LiveData<MessageDto> = _addPost

    private val _deletePost = MutableLiveData<MessageDto>()
    val deletePost: LiveData<MessageDto> = _deletePost

    private val _updatePost = MutableLiveData<MessageDto>()
    val updatePost: LiveData<MessageDto> = _updatePost

    private val _postView = MutableLiveData<PostViewDto>()
    val postView: LiveData<PostViewDto> = _postView

    private val _postList = MutableLiveData<List<PostList>>()
    val postList: LiveData<List<PostList>> = _postList

    private val _myPostList = MutableLiveData<List<PostMyList>>()
    val myPostList: LiveData<List<PostMyList>> = _myPostList

    private val _addComment = MutableLiveData<MessageDto>()
    val addComment: LiveData<MessageDto> = _addComment

    private val _deleteComment = MutableLiveData<MessageDto>()
    val deleteComment: LiveData<MessageDto> = _deleteComment

    private val _updateComment = MutableLiveData<MessageDto>()
    val updateComment: LiveData<MessageDto> = _updateComment

    private val _addReply = MutableLiveData<MessageDto>()
    val addReply: LiveData<MessageDto> = _addReply

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshed = MutableLiveData<Boolean>()
    val isRefreshed: LiveData<Boolean> = _isRefreshed

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _isAll = MutableLiveData<Boolean>()
    val isAll: LiveData<Boolean> = _isAll

    fun addPost(postAddDto: PostAddDto) {
        postRepository.addPost(postAddDto) { result ->
            handleResult(result, _addPost, _errorMessage)
        }
    }

    fun deletePost(postId: Int) {
        postRepository.deletePost(postId) { result ->
            handleResult(result, _deletePost, _errorMessage)
        }
    }

    fun updatePost(postId: Int, postUpdateDto: PostUpdateDto) {
        postRepository.updatePost(postId, postUpdateDto) { result ->
            handleResult(result, _updatePost, _errorMessage)
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

    fun getMyPostList(page: Int, limit: Int, refresh: Boolean = false) {
        postRepository.getMyPostList(page, limit) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    if (refresh) {
                        _myPostList.postValue(result.data.postList)
                    } else {
                        setLoading(true)
                        _myPostList.postValue(_myPostList.value.orEmpty() + result.data.postList)
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
            handleResult(result, _addComment, _errorMessage)
        }
    }

    fun deleteComment(commentId: Int) {
        postRepository.deleteComment(commentId) { result ->
            handleResult(result, _deleteComment, _errorMessage)
        }
    }

    fun updateComment(commentId: Int, commentUpdateDto: CommentUpdateDto) {
        postRepository.updateComment(commentId, commentUpdateDto) { result ->
            handleResult(result, _updateComment, _errorMessage)
        }
    }

    fun addReply(replyAddDto: ReplyAddDto) {
        postRepository.addReply(replyAddDto) { result ->
            handleResult(result, _addReply, _errorMessage)
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setIsAll(isAll: Boolean) {
        _isAll.value = isAll
    }

    private fun handleResult(result: ResultUtil<MessageDto>, successLiveData: MutableLiveData<MessageDto>, errorLiveData: MutableLiveData<Event<String>>) {
        when (result) {
            is ResultUtil.Success -> { successLiveData.postValue(result.data) }
            is ResultUtil.Error -> { errorLiveData.postValue(Event(result.error)) }
            is ResultUtil.NetworkError -> { errorLiveData.postValue(Event("네트워크 오류가 발생했습니다.")) }
        }
    }
}