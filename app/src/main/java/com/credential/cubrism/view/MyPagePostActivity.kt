package com.credential.cubrism.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMypageMypostBinding

class MyPagePostActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageMypostBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
    }
}