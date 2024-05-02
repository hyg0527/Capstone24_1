package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.databinding.ActivityAddstudyBinding
import com.credential.cubrism.model.dto.StudyGroupCreateDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupTagAdapter2
import com.credential.cubrism.view.adapter.TagDeleteClickListener
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyCreateActivity : AppCompatActivity(), TagDeleteClickListener {
    private val binding by lazy { ActivityAddstudyBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var studyGroupTagAdapter2 : StudyGroupTagAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupView()
        observeViewModel()
    }

    override fun onDeleteClick(position: Int) {
        studyGroupTagAdapter2.deleteItem(position)
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        studyGroupTagAdapter2 = StudyGroupTagAdapter2(this)

        binding.recyclerView.apply {
            adapter = studyGroupTagAdapter2
            addItemDecoration(ItemDecoratorDivider(0, 20, 0, 20, 0, 0, 0))
        }
    }

    private fun setupView() {
        binding.btnAddTag.setOnClickListener {
            binding.editTag.text?.let {
                if (it.contains(" ")) {
                    Toast.makeText(this, "태그에 공백을 포함할 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else if (it.isNotEmpty()) {
                    studyGroupTagAdapter2.addItem(it.toString())
                    binding.editTag.text?.clear()
                }
            }
        }

        binding.btnCreate.setOnClickListener {
            val groupName = binding.editName.text.toString()
            val groupDescription = binding.editDescription.text.toString()
            val maxMembers = binding.editMember.text.toString().toIntOrNull()
            val tags = studyGroupTagAdapter2.getItemList()

            if (groupName.isEmpty()) {
                Toast.makeText(this, "스터디 그룹 명을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (maxMembers == null || maxMembers <= 1) {
                Toast.makeText(this, "스터디 그룹 인원 수를 2명 이상 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (groupDescription.isEmpty()) {
                Toast.makeText(this, "스터디 그룹 설명을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            studyGroupViewModel.createStudyGroup(StudyGroupCreateDto(groupName, groupDescription, maxMembers, tags))
        }

        binding.editMember.formatNumber()
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            createGroup.observe(this@StudyCreateActivity) {
                Toast.makeText(this@StudyCreateActivity, it.message, Toast.LENGTH_SHORT).show().also { finish() }
            }

            errorMessage.observe(this@StudyCreateActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@StudyCreateActivity, message, Toast.LENGTH_SHORT).show()
                    binding.progressIndicator.visibility = View.GONE
                }
            }
        }
    }

    // 인원수를 0으로 시작하는 것을 방지
    private fun EditText.formatNumber() {
        addTextChangedListener { text ->
            if (text.toString().startsWith("0"))
                setText(getText().toString().substring(1))
        }
    }
}