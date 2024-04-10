package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.credential.cubrism.databinding.ActivityQnaPostingBinding
import com.credential.cubrism.databinding.DialogCategoryBinding
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.view.adapter.QualificationAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

class QnaPostingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQnaPostingBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private val qualfiicationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val qualificationAdapter = QualificationAdapter()
    private lateinit var bottomSheetDialog: BottomSheetDialog

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
        qualfiicationViewModel.getQualificationList()

        binding.btnAdd.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()
            val category = binding.txtCategory.text.toString()
            val images = emptyList<String?>() // 임시

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val postAddDto = PostAddDto(title, content, boardId, category, images)
            postViewModel.addPost(postAddDto)
        }

        binding.imgDropDown.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun observeViewModel() {
        postViewModel.addPost.observe(this) {
            setResult(RESULT_OK).also { finish() }
        }

        qualfiicationViewModel.qualificationList.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    binding.txtCategory.text = result.data.random().name
                    qualificationAdapter.setItemList(result.data)
                }
                is ResultUtil.Error -> { Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }

        postViewModel.errorMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        }
    }
}