package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMypageApplyhistoryBinding
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupJoinAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class MyPageStudyGroupApplyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageApplyhistoryBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private val studyGroupJoinAdapter = StudyGroupJoinAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        studyGroupViewModel.getStudyGroupJoinList()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = studyGroupJoinAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 80, Color.parseColor("#E0E0E0")))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            joinList.observe(this@MyPageStudyGroupApplyActivity) {
                if (it.isNotEmpty())
                    binding.txtNoJoin.visibility = View.GONE
                studyGroupJoinAdapter.setItemList(it)
            }

            errorMessage.observe(this@MyPageStudyGroupApplyActivity) {
                it.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@MyPageStudyGroupApplyActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}