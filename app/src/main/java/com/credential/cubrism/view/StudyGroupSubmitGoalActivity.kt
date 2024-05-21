package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivitySubmitGoalBinding

class StudyGroupSubmitGoalActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySubmitGoalBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setUpView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setUpView() {
        binding.submitButton.setOnClickListener {
            Toast.makeText(this, "목표 인증 글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}