package com.credential.cubrism.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMystudyBinding
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupMyAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class MyStudyListActivity : AppCompatActivity(), StudyGroupMyAdapter.OnViewClickListener {
    private val binding by lazy { ActivityMystudyBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var studyGroupMyAdapter: StudyGroupMyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun setOnViewClick(item: GroupList) {
        val intent = Intent(this, StudyActivity::class.java)
        intent.putExtra("studyGroupId", item.studyGroupId)
        startActivity(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupView() {
        studyGroupViewModel.getMyStudyGroupList()

        binding.btnAdd.setOnClickListener { // 스터디 그룹 만들기 화면 으로 이동
            startActivity(Intent(this, StudyCreateActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        studyGroupMyAdapter = StudyGroupMyAdapter(this)

        binding.recyclerView.apply {
            adapter = studyGroupMyAdapter
            setHasFixedSize(true)
            addItemDecoration(ItemDecoratorDivider(this@MyStudyListActivity, 0, 0, 0, 0, 1, 0, Color.parseColor("#E0E0E0")))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            studyGroupMyList.observe(this@MyStudyListActivity) {
                binding.progressIndicator.hide()
                studyGroupMyAdapter.setItemList(it)
                binding.txtNoJoin.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }

            errorMessage.observe(this@MyStudyListActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@MyStudyListActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}