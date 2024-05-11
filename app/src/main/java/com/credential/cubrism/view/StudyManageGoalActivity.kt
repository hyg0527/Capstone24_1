package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityStudygroupGoalBinding
import com.credential.cubrism.databinding.DialogGoalAddBinding
import com.credential.cubrism.model.dto.StudyGroupGoalListDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.StudyGroupGoalAdapter
import com.credential.cubrism.view.adapter.StudyGroupGoalClickListener
import com.credential.cubrism.view.adapter.StudyGroupGoalType
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyManageGoalActivity : AppCompatActivity(), StudyGroupGoalClickListener {
    private val binding by lazy { ActivityStudygroupGoalBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var studyGroupGoalAdapter: StudyGroupGoalAdapter

    private lateinit var goalAddDialog: GoalAddDialog

    private val groupId by lazy { intent.getIntExtra("groupId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        setupDialog()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onGoalClick(item: StudyGroupGoalListDto) {
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
        studyGroupViewModel.getGoalList(groupId)

        binding.btnAddGoal.setOnClickListener {
            if (studyGroupGoalAdapter.getItemSize() >= 3) {
                Toast.makeText(this, "목표 개수는 3개까지 작성 가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: 목표 추가
                goalAddDialog.show(supportFragmentManager, "GoalAddDialog")
            }
        }
    }

    private fun setupDialog() {
        goalAddDialog = GoalAddDialog(this,
            onConfirm = {
                Toast.makeText(this, "title: $it", Toast.LENGTH_SHORT).show()
            }
        )
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

                if (list.isEmpty()) {
                    binding.txtNoGoal.visibility = View.VISIBLE
                } else {
                    binding.txtNoGoal.visibility = View.GONE
                    studyGroupGoalAdapter.setItemList(list)
                }

                if (list.size >= 3) {
                    binding.btnAddGoal.visibility = View.GONE
                } else {
                    binding.btnAddGoal.visibility = View.VISIBLE
                }
            }

            deleteGoal.observe(this@StudyManageGoalActivity) {
                Toast.makeText(this@StudyManageGoalActivity, it.message, Toast.LENGTH_SHORT).show()
                studyGroupViewModel.getGoalList(groupId)
            }

            errorMessage.observe(this@StudyManageGoalActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@StudyManageGoalActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun callAddDialog(adapter: GoalAdapter) { // 목표 추가 다이얼로그 호출
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogGoalAddBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
            .setPositiveButton("추가") { dialog, _ ->
                if (dialogBinding.editTextGoalTitle.text.isEmpty()) {
                    Toast.makeText(this, "목표 제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                else {
                    adapter.addItem(adapter.getItem().size + 1, dialogBinding.editTextGoalTitle.text.toString())
                    Toast.makeText(this, "목표가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}