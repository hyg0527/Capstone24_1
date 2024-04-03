package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityQnaViewBinding
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.view.adapter.OnReplyClickListener
import com.credential.cubrism.view.adapter.PostCommentAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QnaViewActivity : AppCompatActivity(), OnReplyClickListener {
    private val binding by lazy { ActivityQnaViewBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private lateinit var postCommentAdapter : PostCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val postId = intent.getIntExtra("postId", -1)
        val boardName = intent.getStringExtra("boardName")

        if (postId != -1 && boardName != null) {
            postViewModel.getPostView(boardName, postId)
        }

        /* TODO
            - 임시로 지정해둔 이메일 나중에 로그인한 이메일로 변경
            - 키보드 올라올 때 맨 아래로 스크롤
         */
        postCommentAdapter = PostCommentAdapter("10000000@test.com", this)
        binding.recyclerView.apply {
            adapter = postCommentAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
        }

        postViewModel.postView.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val postView = result.data
                    Glide.with(this).load(postView.profileImageUrl).error(R.drawable.profil_image).into(binding.imgProfile)
                    binding.txtNickname.text = postView.nickname
                    binding.txtCategory.text = postView.category
                    binding.txtTitle.text = postView.title
                    binding.txtContent.text = postView.content.replace(" ", "\u00A0")

                    postCommentAdapter.setItemList(postView.comments)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                is ResultUtil.Error -> { Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (postId != -1 && boardName != null) {
                postViewModel.getPostView(boardName, postId)
            }
            binding.swipeRefreshLayout.isRefreshing = true
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onReplyClick(viewHolder: RecyclerView.ViewHolder, nickname: String) {
        binding.txtReply.text = nickname
        binding.imgReply.visibility = View.VISIBLE
        binding.txtReply.visibility = View.VISIBLE
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(this, R.color.lightblue))
    }
}