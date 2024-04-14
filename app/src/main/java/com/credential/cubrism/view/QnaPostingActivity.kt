package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.databinding.ActivityQnaPostingBinding
import com.credential.cubrism.databinding.DialogCategoryBinding
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostUpdateDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.QualificationAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

class QnaPostingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQnaPostingBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val qualificationAdapter = QualificationAdapter()
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val postState by lazy { intent.getStringExtra("postState") }
    private val postId by lazy { intent.getIntExtra("postId", -1) }

    private val boardId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupBottomSheetDialog()
        setupView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupBottomSheetDialog() {
        val bottomSheetBinding = DialogCategoryBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.recyclerView.apply {
            adapter = qualificationAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.GRAY))
            setHasFixedSize(true)
        }

        bottomSheetBinding.editSearch.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            qualificationAdapter.filter.filter(text)
        })

        qualificationAdapter.setOnItemClickListener { item, _ ->
            binding.txtCategory.text = item.name
            bottomSheetDialog.dismiss()
        }
    }

    private fun setupView() {
        qualificationViewModel.getQualificationList()

        when (postState) {
            "add" -> {
                binding.txtTitle.text = "글 작성"
                binding.btnAdd.text = "등록"
            }
            "update" -> {
                if (postId != -1) postViewModel.getPostView(postId)
                binding.txtTitle.text = "글 수정"
                binding.btnAdd.text = "수정"
            }
        }

        if (postState == "update" && postId != -1)
            postViewModel.getPostView(postId)

        binding.btnAdd.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()
            val category = binding.txtCategory.text.toString()
            val images = emptyList<String?>() // 임시
            val removedImages = emptyList<String?>() // 임시

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (postState) {
                "add" ->  postViewModel.addPost(PostAddDto(title, content, boardId, category, images))
                "update" -> postViewModel.updatePost(postId, PostUpdateDto(title, content, category, images, removedImages))
            }
        }

        binding.imgDropDown.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun observeViewModel() {
        postViewModel.apply {
            addPost.observe(this@QnaPostingActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            updatePost.observe(this@QnaPostingActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            postView.observe(this@QnaPostingActivity) {
                binding.editTitle.setText(it.title)
                binding.editContent.setText(it.content)
                binding.txtCategory.text = it.category
            }

            errorMessage.observe(this@QnaPostingActivity) {
                it.getContentIfNotHandled()?.let { message -> Toast.makeText(this@QnaPostingActivity, message, Toast.LENGTH_SHORT).show() }
            }
        }

        qualificationViewModel.apply {
            qualificationList.observe(this@QnaPostingActivity) { result ->
                if (postState == "add") binding.txtCategory.text = result.random().name
                qualificationAdapter.setItemList(result)
            }

            errorMessage.observe(this@QnaPostingActivity) { message ->
                message.getContentIfNotHandled()?.let { Toast.makeText(this@QnaPostingActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}