package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityQnaViewBinding
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.CommentUpdateDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.view.adapter.OnReplyClickListener
import com.credential.cubrism.view.adapter.PostCommentAdapter
import com.credential.cubrism.view.utils.CommentState
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QnaViewActivity : AppCompatActivity(), OnReplyClickListener {
    private val binding by lazy { ActivityQnaViewBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }

    private lateinit var postCommentAdapter : PostCommentAdapter

    private val postId by lazy { intent.getIntExtra("postId", -1) }
    private val myEmail by lazy { intent.getStringExtra("myEmail") }
    private val imm by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    private var commentState = CommentState.ADD
    private var commentId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupView()
        observeViewModel()
        getPostView()
    }

    override fun onReplyClick(nickname: String) {
        binding.txtReply.text = nickname
        binding.imgReply.visibility = View.VISIBLE
        binding.txtReply.visibility = View.VISIBLE
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        postCommentAdapter = PostCommentAdapter(this@QnaViewActivity, myEmail, this)
        binding.recyclerView.apply {
            adapter = postCommentAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            getPostView()
        }
    }

    private fun setupView() {
        binding.btnSend.setOnClickListener {
            if (binding.editComment.text.trim().isEmpty()) {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (commentState) {
                CommentState.ADD -> postViewModel.addComment(CommentAddDto(postId, binding.editComment.text.toString()))
                CommentState.UPDATE -> {
                    if (commentId != -1) {
                         postViewModel.updateComment(commentId, CommentUpdateDto(binding.editComment.text.toString()))
                    }
                }
                CommentState.REPLY -> {}
            }

            commentId = -1
            commentState = CommentState.ADD
            binding.editComment.text.clear()
            imm.hideSoftInputFromWindow(binding.editComment.windowToken, 0) // 키보드 내리기
        }
    }

    private fun observeViewModel() {
        postViewModel.postView.observe(this) { result ->
            Glide.with(this).load(result.profileImageUrl)
                .error(R.drawable.profil_image)
                .fallback(R.drawable.profil_image)
                .dontAnimate()
                .into(binding.imgProfile)
            binding.txtNickname.text = "  ${result.nickname}  "
            binding.txtCategory.text = result.category
            binding.txtTitle.text = result.title
            binding.txtContent.text = result.content.replace(" ", "\u00A0")

            postCommentAdapter.setItemList(result.comments)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        postViewModel.addComment.observe(this) {
            getPostView()
        }

        postViewModel.updateComment.observe(this) {
            getPostView()
        }

        postViewModel.deleteComment.observe(this) {
            getPostView()
        }

        postViewModel.clickedItem.observe(this) {
            commentId = it.first.commentId
            when (it.second) {
                "수정" -> {
                    commentState = CommentState.UPDATE
                    binding.editComment.setText(it.first.content)
                    binding.editComment.setSelection(binding.editComment.text.length)
                    binding.editComment.post {
                        if (binding.editComment.isEnabled) {
                            binding.editComment.requestFocus()
                            imm.showSoftInput(binding.editComment, 0)
                        }
                    }
                }
                "삭제" -> {
                    postViewModel.deleteComment(commentId)
                }
            }
        }

        postViewModel.errorMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getPostView() {
        if (postId != -1) {
            postViewModel.getPostView(postId)
            binding.swipeRefreshLayout.isRefreshing = true
        }
    }
}