package com.credential.cubrism.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityStudygroupManagegoalacceptBinding
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitListDto
import com.credential.cubrism.view.adapter.StudyGroupGoalAcceptAdapter

class StudyManageGoalAcceptActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudygroupManagegoalacceptBinding.inflate(layoutInflater) }

    private val goalAcceptAdapter = StudyGroupGoalAcceptAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()

        goalAcceptAdapter.setItem(StudyGroupGoalSubmitListDto(0, "user1", "", "description", "", "0000-00-00", "title"))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = goalAcceptAdapter

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}