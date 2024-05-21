package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityStudygroupManagegoalacceptBinding
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitListDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.GroupSubmitAcceptClickListener
import com.credential.cubrism.view.adapter.GroupSubmitDenyClickListener
import com.credential.cubrism.view.adapter.StudyGroupGoalAcceptAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyManageGoalAcceptActivity : AppCompatActivity(), GroupSubmitAcceptClickListener, GroupSubmitDenyClickListener {
    private val binding by lazy { ActivityStudygroupManagegoalacceptBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var goalAcceptAdapter: StudyGroupGoalAcceptAdapter

    private val groupId by lazy { intent.getIntExtra("groupId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        getGoalSubmitList()
    }

    override fun onAcceptClick(item: StudyGroupGoalSubmitListDto) {
        AlertDialog.Builder(this).apply {
            setMessage("승인하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                studyGroupViewModel.acceptGoalSubmit(item.userGoalId)
            }
            show()
        }
    }

    override fun onDenyClick(item: StudyGroupGoalSubmitListDto) {
        AlertDialog.Builder(this).apply {
            setMessage("거절하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                studyGroupViewModel.denyGoalSubmit(item.userGoalId)
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

    private fun setupRecyclerView() {
        goalAcceptAdapter = StudyGroupGoalAcceptAdapter(this, this)

        binding.recyclerView.apply {
            adapter = goalAcceptAdapter
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            goalSubmitList.observe(this@StudyManageGoalAcceptActivity) {
                if (it.isEmpty())
                    binding.txtNoSubmit.visibility = View.VISIBLE

                binding.progressIndicator.hide()
                goalAcceptAdapter.setItemList(it)
            }

            acceptGoalSubmit.observe(this@StudyManageGoalAcceptActivity) {
                Toast.makeText(this@StudyManageGoalAcceptActivity, it.message, Toast.LENGTH_SHORT).show()
                getGoalSubmitList()
            }

            denyGoalSubmit.observe(this@StudyManageGoalAcceptActivity) {
                Toast.makeText(this@StudyManageGoalAcceptActivity, it.message, Toast.LENGTH_SHORT).show()
                getGoalSubmitList()
            }

            errorMessage.observe(this@StudyManageGoalAcceptActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@StudyManageGoalAcceptActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getGoalSubmitList() {
        if (groupId != -1)
            studyGroupViewModel.getGoalSubmitList(groupId)
    }
}