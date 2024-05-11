package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyManageBinding

class StudyManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyManageBinding.inflate(layoutInflater) }

    private val groupId by lazy { intent.getIntExtra("groupId", -1) }
    private val dday by lazy { intent.getStringExtra("dday") }
    private val ddayTitle by lazy { intent.getStringExtra("ddayTitle") }

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
            intent.putExtra("ddayTitle", ddayTitle)
            intent.putExtra("dday", dday)
            startActivity(intent)
        }
        binding.manageAccept.setOnClickListener {
            val intent = Intent(this, StudyManageAcceptActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
    }

    fun changeFragmentManage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
//            .replace(binding.fragmentContainerView.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun popBackStackFragment() {
        supportFragmentManager.popBackStack()
    }
}