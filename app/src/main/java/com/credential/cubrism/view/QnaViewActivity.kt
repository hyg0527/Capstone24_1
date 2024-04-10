package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityQnaViewBinding
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.view.adapter.OnReplyClickListener
import com.credential.cubrism.view.adapter.PostCommentAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QnaViewActivity : AppCompatActivity(), OnReplyClickListener {
    private val binding by lazy { ActivityQnaViewBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }

    private lateinit var postCommentAdapter : PostCommentAdapter

    private val postId by lazy { intent.getIntExtra("postId", -1) }
    private val myEmail by lazy { intent.getStringExtra("myEmail") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupView()
        observeViewModel()
        getPostView()
    }

    override fun onReplyClick(viewHolder: RecyclerView.ViewHolder, nickname: String) {
        binding.txtReply.text = nickname
        binding.imgReply.visibility = View.VISIBLE
        binding.txtReply.visibility = View.VISIBLE
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(this, R.color.lightblue))
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        postCommentAdapter = PostCommentAdapter(myEmail, this)
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.btnSend.setOnClickListener {
            if (binding.editComment.text.trim().isEmpty()) {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                postViewModel.addComment(CommentAddDto(postId, binding.editComment.text.toString()))
                binding.editComment.text.clear()
                // 키보드 내리기
                imm.hideSoftInputFromWindow(binding.editComment.windowToken, 0)
            }
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