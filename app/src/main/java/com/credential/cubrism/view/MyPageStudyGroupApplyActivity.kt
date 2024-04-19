package com.credential.cubrism.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMypageApplyhistoryBinding

class MyPageStudyGroupApplyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageApplyhistoryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
    }
}