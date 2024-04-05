package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMystudyBinding

class MyStudyListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMystudyBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnAdd.setOnClickListener { // 스터디 그룹 만들기 화면 으로 이동
                startActivity(Intent(root.context, StudyCreateActivity::class.java))
            }
            btnEnterStudyGroup.setOnClickListener { // 스터디 그룹 진입 버튼(임시)
                startActivity(Intent(root.context, StudyActivity::class.java))
            }
        }

    }
}