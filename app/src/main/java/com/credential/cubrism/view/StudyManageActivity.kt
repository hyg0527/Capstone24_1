package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityStudyManageBinding

class StudyManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyManageBinding.inflate(layoutInflater) }

    private val groupId by lazy { intent.getIntExtra("groupId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.manageGoal.setOnClickListener {
            val intent = Intent(this, StudyManageGoalActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
        binding.manageDday.setOnClickListener {
            val intent = Intent(this, StudyManageDDayActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
        binding.manageAccept.setOnClickListener {
            val intent = Intent(this, StudyManageAcceptActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
        binding.manageAcceptGoal.setOnClickListener {
            val intent = Intent(this, StudyManageGoalAcceptActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
    }
}