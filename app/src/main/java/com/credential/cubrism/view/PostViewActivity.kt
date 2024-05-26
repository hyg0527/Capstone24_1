package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityPostViewBinding
import com.credential.cubrism.databinding.DialogMenuBinding
import com.credential.cubrism.model.dto.CommentAddDto
import com.credential.cubrism.model.dto.CommentUpdateDto
import com.credential.cubrism.model.dto.Comments
import com.credential.cubrism.model.dto.MenuDto
import com.credential.cubrism.model.dto.ReplyAddDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.view.adapter.MenuAdapter
import com.credential.cubrism.view.adapter.PostCommentAdapter
import com.credential.cubrism.view.adapter.PostImageAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.view.utils.KeyboardVisibilityUtils
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import io.noties.markwon.Markwon

enum class CommentState {
    ADD, UPDATE, REPLY
}

class PostViewActivity : AppCompatActivity(), PostCommentAdapter.OnViewClickListener {
    private val binding by lazy { ActivityPostViewBinding.inflate(layoutInflater) }

    private val myApplication = MyApplication.getInstance()

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }

    private lateinit var postCommentAdapter : PostCommentAdapter
    private val postImageAdapter = PostImageAdapter()
    private val menuAdapter = MenuAdapter()

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var powerMenu : PowerMenu
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var markwon: Markwon

    private val isLoggedIn = myApplication.getUserData().getLoginStatus()
    private val myEmail = myApplication.getUserData().getEmail()
    private val postId by lazy { intent.getIntExtra("postId", -1) }
    private val imm by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    private var commentState = CommentState.ADD
    private var commentId: Int? = null
    private var comment: String? = null

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getPostView()
            binding.swipeRefreshLayout.isRefreshing = true
            setResult(RESULT_OK)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPress()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setupToolbar()
        setupBottomSheetDialog()
        setupRecyclerView()
        setupView()
        observeViewModel()
        getPostView()
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    override fun setOnViewClick(viewId: Int, item: Comments) {
        when (viewId) {
            R.id.layout -> {
                commentId = item.commentId
                comment = item.content
                if (isLoggedIn)
                    bottomSheetDialog.show()
            }
            R.id.imgReplyTo -> {
                if (isLoggedIn) {
                    item.nickname?.let {
                        commentState = CommentState.REPLY
                        commentId = item.commentId

                        binding.layoutReply.visibility = View.VISIBLE
                        binding.txtReply.text = it
                        binding.editComment.hint = "답글 입력"

                        binding.editComment.requestFocus()
                        imm.showSoftInput(binding.editComment, InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { backPress() }
    }

    private fun setupBottomSheetDialog() {
        val bottomSheetBinding = DialogMenuBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        val menuList = listOf(
            MenuDto(R.drawable.icon_edit2, "수정하기"),
            MenuDto(R.drawable.icon_delete2, "삭제하기")
        )

        menuAdapter.setItemList(menuList)

        bottomSheetBinding.recyclerView.apply {
            adapter = menuAdapter
            itemAnimator = null
            setHasFixedSize(true)
        }

        menuAdapter.setOnItemClickListener { item, _ ->
            when (item.text) {
                "수정하기" -> {
                    commentState = CommentState.UPDATE
                    binding.editComment.setText(comment)
                    binding.editComment.text?.length?.let { selection ->
                        binding.editComment.setSelection(selection)
                    }
                    binding.editComment.post {
                        if (binding.editComment.isEnabled) {
                            binding.editComment.requestFocus()
                            imm.showSoftInput(binding.editComment, 0)
                        }
                    }
                }
                "삭제하기" -> {
                    AlertDialog.Builder(this).apply {
                        setMessage("댓글을 삭제하시겠습니까?")
                        setNegativeButton("취소", null)
                        setPositiveButton("삭제") { _, _ ->
                            commentId?.let {
                                postViewModel.deleteComment(it)
                            }
                        }
                        show()
                    }
                }
            }
            bottomSheetDialog.dismiss()
        }
    }

    private fun setupRecyclerView() {
        postCommentAdapter = PostCommentAdapter(myEmail, this)
        binding.recyclerComment.apply {
            adapter = postCommentAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(this@PostViewActivity, 0, 4, 0, 0, 0, 0, null))
        }

        binding.recyclerImage.apply {
            adapter = postImageAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(this@PostViewActivity, 0, 0, 0, 12, 0, 0, null))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            getPostView()
        }

        postImageAdapter.setOnItemClickListener { _, position ->
            val intent = Intent(this, PhotoViewActivity::class.java)
            intent.putStringArrayListExtra("url", postImageAdapter.getItemList())
            intent.putExtra("position", position)
            intent.putExtra("download", true)
            startActivity(intent)
        }
    }

    private fun setupView() {
        binding.editComment.isEnabled = isLoggedIn
        binding.btnSend.isEnabled = isLoggedIn

        markwon = Markwon.create(this)

        powerMenu = PowerMenu.Builder(this)
            .addItemList(listOf(
                PowerMenuItem("수정", false),
                PowerMenuItem("삭제", false)
            ))
            .setAnimation(MenuAnimation.DROP_DOWN)
            .setMenuRadius(20f)
            .setMenuShadow(10f)
            .setMenuColor(Color.WHITE)
            .setShowBackground(false)
            .setLifecycleOwner(this)
            .build()

        powerMenu.setOnMenuItemClickListener { position, _ ->
            when (position) {
                0 -> {
                    val intent = Intent(this, PostUpdateActivity::class.java)
                    intent.putExtra("postId", postId)
                    startForRegisterResult.launch(intent)
                }
                1 -> {
                    AlertDialog.Builder(this).apply {
                        setMessage("게시글을 삭제하시겠습니까?")
                        setNegativeButton("취소", null)
                        setPositiveButton("삭제") { _, _ ->
                            postViewModel.deletePost(postId)
                        }
                        show()
                    }
                }
            }
            powerMenu.dismiss()
        }

        binding.btnMenu.setOnClickListener {
            powerMenu.showAsDropDown(binding.btnMenu)
        }

        binding.btnSend.setOnClickListener {
            if (binding.editComment.text?.trim()?.isEmpty() == true) {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (commentState) {
                CommentState.ADD -> postViewModel.addComment(CommentAddDto(postId, binding.editComment.text.toString()))
                CommentState.UPDATE -> {
                    commentId?.let {
                        postViewModel.updateComment(it, CommentUpdateDto(binding.editComment.text.toString()))
                    }
                }
                CommentState.REPLY -> {
                    commentId?.let {
                        postViewModel.addReply(ReplyAddDto(postId, it, binding.editComment.text.toString()))
                    }
                }
            }

            clearComment()
        }

        binding.btnCancel.setOnClickListener {
            clearComment()
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = {
                binding.editComment.maxLines = 3
            },
            onHideKeyboard = {
                binding.editComment.maxLines = 1
            }
        )
    }

    private fun observeViewModel() {
        postViewModel.apply {
            postView.observe(this@PostViewActivity) { result ->
                Glide.with(this@PostViewActivity).load(result.profileImageUrl)
                    .error(R.drawable.profile_blue)
                    .fallback(R.drawable.profile_blue)
                    .dontAnimate()
                    .into(binding.imgProfile)
                binding.txtNickname.text = "  ${result.nickname ?: "(알수없음)"}  "
                binding.txtCategory.text = result.category
                binding.txtTitle.text = result.title
                binding.txtContent.text = result.content.replace(" ", "\u00A0")

                binding.btnMenu.visibility = if (isLoggedIn && result.email == myEmail) View.VISIBLE else View.GONE
                binding.layoutFooter.visibility = if (isLoggedIn && result.nickname != null) View.VISIBLE else View.GONE

                postCommentAdapter.setItemList(result.comments)
                postImageAdapter.setItemList(result.images)

                if (result.aiComment == null) {
                    // 답변이 null일 경우 (아직 생성 중)
                    binding.gptAnswer.text = "답변을 생성 중입니다.."
                    binding.gptAnswer.setTypeface(null, Typeface.ITALIC)
                } else {
                    // 답변이 null이 아닐 경우 (생성 완료)
                    binding.gptAnswer.setTypeface(null, Typeface.NORMAL)
                    val node = markwon.parse(result.aiComment)
                    val markdownContent = markwon.render(node)
                    markwon.setParsedMarkdown(binding.gptAnswer, markdownContent)
                }

                binding.swipeRefreshLayout.isRefreshing = false
            }

            deletePost.observe(this@PostViewActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            addComment.observe(this@PostViewActivity) {
                getPostView()
            }

            updateComment.observe(this@PostViewActivity) {
                getPostView()
            }

            deleteComment.observe(this@PostViewActivity) {
                getPostView()
            }

            addReply.observe(this@PostViewActivity) {
                getPostView()
            }

            errorMessage.observe(this@PostViewActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    if (!message.lowercase().contains("jwt"))
                        Toast.makeText(this@PostViewActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPostView() {
        if (postId != -1) {
            postViewModel.getPostView(postId)
            binding.swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun clearComment() {
        commentState = CommentState.ADD
        commentId = -1

        binding.layoutReply.visibility = View.GONE
        binding.editComment.text?.clear()
        binding.editComment.hint = "댓글 입력"

        binding.editComment.clearFocus()
        imm.hideSoftInputFromWindow(binding.editComment.windowToken, 0) // 키보드 내리기
    }

    private fun backPress() {
        if (powerMenu.isShowing) {
            powerMenu.dismiss()
        } else if (binding.editComment.text?.isNotEmpty() == true || commentState == CommentState.REPLY || commentState == CommentState.UPDATE) {
            AlertDialog.Builder(this).apply {
                setMessage("댓글 작성을 취소하시겠습니까?")
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    clearComment()
                }
                show()
            }
        } else {
            finish()
        }
    }
}