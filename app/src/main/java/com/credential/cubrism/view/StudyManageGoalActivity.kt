package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityStudygroupGoalBinding
import com.credential.cubrism.model.dto.GoalsDto
import com.credential.cubrism.model.dto.StudyGroupAddGoalDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupGoalAdapter
import com.credential.cubrism.view.adapter.StudyGroupGoalType
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyManageGoalActivity : AppCompatActivity(), StudyGroupGoalAdapter.OnViewClickListener {
    private val binding by lazy { ActivityStudygroupGoalBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var studyGroupGoalAdapter: StudyGroupGoalAdapter

    private val studyGroupId by lazy { intent.getIntExtra("groupId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun setOnViewClick(item: GoalsDto) {
        AlertDialog.Builder(this).apply {
            setTitle(item.goalName)
            setMessage("목표를 삭제하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                studyGroupViewModel.deleteGoal(item.goalId)
            }
            show()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        studyGroupViewModel.getGoalList(studyGroupId)

        binding.btnAddGoal.setOnClickListener {
            if (studyGroupGoalAdapter.getItemSize() >= 3) {
                Toast.makeText(this, "목표는 3개까지 추가 가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        GoalAddDialog(this,
            onConfirm = {
                studyGroupViewModel.addGoal(StudyGroupAddGoalDto(studyGroupId, it))
            }
        ).show(supportFragmentManager, "GoalAddDialog")
    }

    private fun setupRecyclerView() {
        studyGroupGoalAdapter = StudyGroupGoalAdapter(StudyGroupGoalType.GOAL_MANAGE, this)

        binding.recyclerView.apply {
            adapter = studyGroupGoalAdapter
            addItemDecoration(ItemDecoratorDivider(0, 60, 0, 0, 0, 0, null))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            goalList.observe(this@StudyManageGoalActivity) { list ->
                binding.progressIndicator.hide()
                studyGroupGoalAdapter.setItemList(list)

                binding.txtNoGoal.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.btnAddGoal.visibility = if (list.size >= 3) View.GONE else View.VISIBLE
            }

            addGoal.observe(this@StudyManageGoalActivity) {
                Toast.makeText(this@StudyManageGoalActivity, it.message, Toast.LENGTH_SHORT).show()
                studyGroupViewModel.getGoalList(studyGroupId)
            }

            deleteGoal.observe(this@StudyManageGoalActivity) {
                Toast.makeText(this@StudyManageGoalActivity, it.message, Toast.LENGTH_SHORT).show()
                studyGroupViewModel.getGoalList(studyGroupId)
            }

            errorMessage.observe(this@StudyManageGoalActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@StudyManageGoalActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}